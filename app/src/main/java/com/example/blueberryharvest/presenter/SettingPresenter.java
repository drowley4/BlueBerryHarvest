package com.example.blueberryharvest.presenter;

import android.content.Context;

import java.sql.SQLException;

public class SettingPresenter extends Presenter {

    public SettingPresenter(Context c) throws SQLException {
        super(c);
    }

    public boolean deletePicker(String id) {
        int tmp =  Integer.parseInt(id.substring(id.lastIndexOf(" ")+1));
        return super.model.deletePicker(tmp);
    }

    public String backup(String date) {
        return super.model.backupDatabase(date);
    }

    public String export(String date) {
        return super.model.exportDatabase(date);
    }

    public void updateExportEmail(String email) {
        super.model.updateExportEmail(email);
    }

    public void updateBackupEmail(String email) {
        super.model.updateBackupEmail(email);
    }

    public String getExportEmail() {
        return super.model.getEmail(-2);
    }

    public String getBackupEmail() {
        return super.model.getEmail(-1);
    }
}
