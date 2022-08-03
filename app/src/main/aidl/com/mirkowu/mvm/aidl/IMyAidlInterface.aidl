// IMyAidlInterface.aidl
package com.mirkowu.mvm.aidl;

import com.mirkowu.mvm.aidl.bean.Book;
// Declare any non-default types here with import statements

interface IMyAidlInterface {

    void addBook(in Book book);

    List<Book> getBookList();
}