package com.zipwhip.binding;

/**
 * Created by IntelliJ IDEA.
 * User: Michael
 * Date: 11/26/11
 * Time: 8:52 PM
 *
 * Categorizes things that can be track their changes and be modified.
 */
public interface Modifiable {

    /**
     * Will set autoCommit to false. Will NOT commit for you if already dirty. Will NOT change the dirty state.
     */
    void beginEdit();

    /**
     * Will set autoCommit to true, and do a commit (if necessary). If you modify the record after an endEdit() call,
     * then it will cause an instant commit.
     *
     * @throws Exception if the commit phase threw the exception, propagates it
     * @return if the commit was successful or not. Gives no indication that a commit was needed. False means 2
     * things: not needed or not successful. True means committed successfully, False means nothing. Check isDirty
     * manually afterwards to be sure.
     */
    boolean endEdit() throws Exception;

    /**
     * If you turn autoCommit on, you can make changes and get events without having to call "commit"
     * This is on by default and generally convenient.
     *
     * @param autoCommit true to auto commit
     * @throws Exception if a commit was attempted but could not be completed
     * @return if commit was successful. See endEdit() and commit() return
     */
    boolean setAutoCommit(boolean autoCommit) throws Exception;

    /**
     * Determines if you must hit "commit" before the event will throw.
     *
     * @return true if autoCommit has been set to true,
     */
    boolean isAutoCommit();

    /**
     * Do we have any non-committed changes?
     *
     * @return true if there are non-committed, otherwise false
     */
    boolean isDirty();

    /**
     * Cancel any changes. Go back to original state.
     */
    void revert();

    /**
     * Validates that this change would be successful (without committing). Generally used as a check to see
     * if the commit WOULD go ahead if requested. (Useful for a GUI that wants realtime checking without
     * forcing an apply).
     *
     * @return if the data is valid or not.
     * @throws Exception only if the data is super invalid that it cannot be determined to be valid/invalid.
     */
    boolean validate() throws Exception;

    /**
     * Commit all your changes, and forget your previous state. If any changes took place because of this, the
     * "change" event is thrown.
     *
     * Will validate all field/values before committing. If a validator throws an exception it will interrupt the
     * commit. Only if all fields are valid will the commit take place.
     *
     * @return if the commit was successful/validated.
     * @throws Exception If validation was not possible, so therefore commit aborted
     */
    boolean commit() throws Exception;

}
