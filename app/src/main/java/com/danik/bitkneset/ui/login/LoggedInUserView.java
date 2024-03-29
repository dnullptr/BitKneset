package com.danik.bitkneset.ui.login;

/**
 * Class exposing authenticated user details to the UI.
 */
class LoggedInUserView {
    private String displayName;
    //data field will be accessible to the UI

    LoggedInUserView(String displayName) {
        this.displayName = displayName;
    } //constructor to set dispname

    String getDisplayName() {
        return displayName;
    } //simple getter
}
