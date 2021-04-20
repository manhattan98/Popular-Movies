package com.exersice.popularmovies.Models.UpdatableModel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import com.exersice.popularmovies.Models.IUpdatableObservableComparableModel;

import java.util.Comparator;

/**
 * base class that implements IUpdatableComparableObservableModel interface in terms of observe policy, fetch source, and compare policy
 * @param <T>
 */
public abstract class AbstractViewModel<T> extends ViewModel implements IUpdatableObservableComparableModel<T> {
    protected final String TAG = getClass().getSimpleName();

    /**
     * interface that specifies observe policy
     * @param <DT> data type that structure is holding
     */
    protected interface ObservableData<DT> {
        /**
         * add new lifecycle-aware observer
         * @param owner lifecycle owner
         * @param observer
         */
        void addObserver(LifecycleOwner owner, UpdateObserver observer);

        /**
         * set method that notifies callbacks after data has changed
         * @param dt
         * @param throwable
         */
        void set(DT dt, Throwable throwable);

        /**
         * @return stored data, should be immutable
         */
        DT get();
    }

    /**
     * interface specifies some comparable data structure
     * @param <DT>
     */
    protected interface SelfComparableList<DT> {
        default void setComparator(Comparator<DT> comparator, long milliseconds, int count) { }
        default Comparator<DT> getComparator() { return (DT o1, DT o2) -> 0; }

        DT getSorted(int index);
        int size();
        void addAll(DT[] c);
        void clear();
    }


    /**
     * abstract class implement ObservableData and allow initialize it with given SelfComparable object
     * @param <DT>
     */
    protected static abstract class ObservableComparableList<DT> implements ObservableData<SelfComparableList<DT>> {
        private SelfComparableList<DT> mSCList;

        protected SelfComparableList<DT> getSCList() {
            return mSCList;
        }

        protected void setSCList(SelfComparableList<DT> SCList) {
            mSCList = SCList;
        }
    }


    // -------------------------------------------------------------------------------------------------------------------


    /**
     * template method that return actual implementation of ObservableData
     * @return
     */
    @NonNull protected abstract ObservableComparableList<T> provideObservable();

    /**
     * template method that return implementation of SelfComparableList
     * @return
     */
    @NonNull protected abstract SelfComparableList<T> provideComparable();

    /**
     * template method that should init required AsyncFetcher parameters and by contract at the end should call setParams()
     */
    protected abstract void initDataFetcher();


    // -------------------------------------------------------------------------------------------------------------------


    /**
     * initialize new Fetcher with given parameters
     * must be called at least once!
     * @param stream
     * @param readsOnFetch
     * @param readsTotalFetch
     */
    protected void setFetcherParams(IFetcher.SynchronousSimpleStream<T> stream, int readsOnFetch, int readsTotalFetch) {
        if (mFetcher != null)
            mFetcher.shutdown();
        mFetcher = new AsyncFetcherAndroid<>(
                stream,
                // FetchCallback lambda definition
                (data, throwable) -> {
                    SelfComparableList<T> intermediateList = mObservableData.get();
                    intermediateList.addAll(data);
                    mObservableData.set(intermediateList, throwable);
                },
                readsOnFetch,
                readsTotalFetch
        );
    }

    /**
     * initialize new AsyncFetcher with given stream from existing AsyncFetcher
     * @param newStream
     * @throws IllegalStateException if existing AsyncFetcher is uninitialized
     */
    protected void setFetcherStream(IFetcher.SynchronousSimpleStream<T> newStream) throws IllegalStateException {
        getFetcher();

        if (mFetcher != null) {
            mFetcher.shutdown();
            mFetcher = new AsyncFetcherAndroid<>(mFetcher, newStream);
        }
        else {
            throw new IllegalStateException("fetcher params does not set yet!");
        }
    }

    protected AsyncFetcher<T> getFetcher() {
        if (mFetcher == null)
            initDataFetcher();

        return mFetcher;
    }

    protected final long getDefaultMilliseconds() {
        if (mDefaultMilliseconds == null)
            throw new IllegalStateException();
        return mDefaultMilliseconds;
    }

    protected final int getDefaultCount() {
        if (mDefaultCount == null)
            throw new IllegalStateException();
        return mDefaultCount;
    }

    /**
     * force observers notification
     */
    protected void notifyObservers() {
        mObservableData.set(mObservableData.get(), null);
    }

    /**
     * never null after non-null initialization
     */
    final protected ObservableComparableList<T> mObservableData;

    /**
     * can be re-initialized
     */
    private AsyncFetcher<T> mFetcher;


    private Long mDefaultMilliseconds;
    private Integer mDefaultCount;


    public AbstractViewModel() {
        mObservableData = provideObservable();
        mObservableData.setSCList(provideComparable());
    }


    @Override
    public void setDefaultMillisAndCount(long milliseconds, int count) {
        mDefaultMilliseconds = milliseconds;
        mDefaultCount = count;
    }

    @Override
    public void observe(LifecycleOwner owner, UpdateObserver observer) {
        mObservableData.addObserver(owner, observer);
    }

    @Override
    public int size() {
        return mObservableData.get().size();
    }

    @Override
    public T get(int position) {
        return mObservableData.get().getSorted(position);
    }

    @Override
    public void fetch(long milliseconds, int count) {
        getFetcher().fetch(milliseconds, count);
    }

    @Override
    public void fetch() {
        fetch(getDefaultMilliseconds(), getDefaultCount());
    }

    @Override
    public boolean available() {
        return getFetcher().available();
    }

    @Override
    public void setComparator(Comparator<T> comparator, long milliseconds, int count) {
        mObservableData.get().setComparator(comparator, milliseconds, count);
    }

    @Override
    public void setComparator(Comparator<T> comparator) {
        setComparator(comparator, getDefaultMilliseconds(), getDefaultCount());
    }

    @Override
    public Comparator<T> getCurrentComparator() {
        return mObservableData.get().getComparator();
    }

}
