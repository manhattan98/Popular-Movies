package com.exersice.popularmovies.Models.UpdatableModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

/**
 * class represent custom list with synchronous thread-safe access and support adding elements only via buckets of elements
 * class interface support access elements via get() method and getSorted(), which returns element, sorted within bucket
 * @param <T>
 */
public class SynchronizedBucketList<T> implements Cloneable {
    private Comparator<T> mComparator;
    private List<T[]> mData;
    private List<Boolean> mIsSorted;
    private int mTotalSize;

    // default comparator implementation does nothing
    private static final Comparator DEFAULT_COMPARATOR = (o1, o2) -> 0;

    public SynchronizedBucketList() {
        mComparator = DEFAULT_COMPARATOR;
        mData = new LinkedList<>();
        mIsSorted = new LinkedList<>();
        mTotalSize = 0;
    }

    /**
     * copy constructor
     * @param synchronizedBucketList source object
     */
    private SynchronizedBucketList(SynchronizedBucketList<T> synchronizedBucketList) {
        synchronized (synchronizedBucketList) {
            mComparator = synchronizedBucketList.mComparator;

            mData = new LinkedList<>();
            mIsSorted = new LinkedList<>();

            for (int i = 0; i < synchronizedBucketList.mData.size(); i++) {
                mData.add(synchronizedBucketList.mData.get(i).clone());
                mIsSorted.add(synchronizedBucketList.mIsSorted.get(i));
            }

            mTotalSize = synchronizedBucketList.mTotalSize;
        }
    }

    @Override
    public synchronized SynchronizedBucketList<T> clone() {
        return new SynchronizedBucketList<>(this);
    }

    // mComparator field doesn't matter when comparing UpdatableData objects
    @Override
    public synchronized boolean equals(Object obj) {
        if (!(obj instanceof SynchronizedBucketList<?>))
            return false;

        SynchronizedBucketList updatableData = (SynchronizedBucketList<?>) obj;
        return mData.equals(updatableData.mData)
                && mIsSorted.equals(updatableData.mIsSorted)
                && mTotalSize == updatableData.mTotalSize;
    }

    public synchronized void addData(T[] data) {
        mData.add(data);
        mIsSorted.add(false);
        mTotalSize += data.length;
    }

    public void add(T element) {
        addData((T[]) new Object[] {element});
    }

    public synchronized int size() {
        return mTotalSize;
    }

    public synchronized T get(int position) {
        BiPosition biPosition = newBiPosition(position);
        return mData.get(biPosition.listPosition)[biPosition.itemPosition];
    }

    public synchronized T getSorted(int position) {
        BiPosition biPosition = newBiPosition(position);
        if (!mIsSorted.get(biPosition.listPosition))
            sortPage(biPosition.listPosition);
        return mData.get(biPosition.listPosition)[biPosition.itemPosition];
    }

    public void clear() {
        mComparator = DEFAULT_COMPARATOR;
        mData.clear();
        mIsSorted.clear();
        mTotalSize = 0;
    }

    public void remove(int position) {
        if (mTotalSize == 0)
            throw new IndexOutOfBoundsException();

        BiPosition biPosition = newBiPosition(position);

        T[] bucket = mData.get(biPosition.listPosition);
        T[] mBucket = (T[]) new Object[bucket.length - 1];

        if (mBucket.length != 0) {
            System.arraycopy(bucket, 0, mBucket, 0, position);
            System.arraycopy(bucket, position + 1, mBucket, position, bucket.length - position - 1);

            mData.set(biPosition.listPosition, mBucket);
        }
        else {
            mData.remove(biPosition.listPosition);
            mIsSorted.remove(biPosition.listPosition);
        }

        mTotalSize--;
    }

    public synchronized void setComparator(Comparator<T> comparator) {
        mComparator = comparator;

        // reset sorted flag
        for (int i = 0; i < mIsSorted.size(); i++)
            mIsSorted.set(i, false);
    }

    public synchronized Comparator<T> currentComparator() {
        return mComparator;
    }

    protected synchronized void sortPage(int page) {
        Arrays.sort(mData.get(page), mComparator);
        mIsSorted.set(page, true);
    }

    protected BiPosition newBiPosition(int position) {
        return new BiPosition(position);
    }


    protected class BiPosition {
        public final int listPosition, itemPosition;

        public BiPosition(int position) {
            int listPosition = 0;
            int itemPosition = position;

            while (listPosition < mTotalSize) {
                if (itemPosition - mData.get(listPosition).length < 0)
                    break;
                itemPosition -= mData.get(listPosition++).length;
            }

            this.listPosition = listPosition;
            this.itemPosition = itemPosition;
        }
    }
}
