package com.exersice.popularmovies.Models;

public interface IUpdatableModel<T> {
    void setDefaultMillisAndCount(long milliseconds, int count);

    /**
     * @return size of abstract linear structure, representing content in model
     */
    int size();

    /**
     * get T object from abstract linear structure, representing content in model
     * @param position linear index of element
     * @return element in model at given abstract index
     */
    T get(int position);

    /**
     * trigger update-content task. when done, observers will be notified, and provided data must be up-to-date at this moment
     * @param milliseconds timeout in milliseconds
     */
    void fetch(long milliseconds, int count);
    void fetch();

    /**
     * if not updatable, no update events will happen, so calling updateContent() will be redundant
     * when model object instantiated, it must be always updatable
     * @return true if updatable, else false
     */
    boolean available();
}
