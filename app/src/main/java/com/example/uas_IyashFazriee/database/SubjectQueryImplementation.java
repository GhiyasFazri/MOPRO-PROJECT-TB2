package com.example.uas_IyashFazriee.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.uas_IyashFazriee.model.Subject;
import java.util.ArrayList;
import java.util.List;
import static com.example.uas_IyashFazriee.util.Constants.SUBJECT_CODE;
import static com.example.uas_IyashFazriee.util.Constants.SUBJECT_CREDIT;
import static com.example.uas_IyashFazriee.util.Constants.SUBJECT_ID;
import static com.example.uas_IyashFazriee.util.Constants.SUBJECT_NAME;
import static com.example.uas_IyashFazriee.util.Constants.TABLE_SUBJECT;

public class SubjectQueryImplementation implements QueryContract.SubjectQuery {

    private DatabaseHelper databaseHelper = DatabaseHelper.getInstance();

    @Override
    public void createSubject(Subject subject, QueryResponse<Boolean> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(SUBJECT_NAME, subject.getName());
        contentValues.put(SUBJECT_CODE, subject.getCode());
        contentValues.put(SUBJECT_CREDIT, subject.getCredit());

        try {
            long id = sqLiteDatabase.insertOrThrow(TABLE_SUBJECT, null, contentValues);
            if(id>0) {
                response.onSuccess(true);
            }
            else {
                response.onFailure("Failed to create subject. Unknown Reason!");
            }
        } catch (SQLiteException e){
            response.onFailure(e.getMessage());
        } finally {
            sqLiteDatabase.close();
        }
    }

    @Override
    public void readAllSubject(QueryResponse<List<Subject>> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        List<Subject> subjectList = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.query(TABLE_SUBJECT, null, null, null, null, null, null);

            if(cursor != null && cursor.moveToFirst()) {
                do {
                    Subject subject = getSubjectFromCursor(cursor);
                    subjectList.add(subject);
                } while (cursor.moveToNext());

                response.onSuccess(subjectList);
            } else {
                response.onFailure("There are no subjects in the database");
            }

        } catch (Exception e){
            response.onFailure(e.getMessage());
        } finally {
            sqLiteDatabase.close();
            if(cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void updateSubject(Subject subject, QueryResponse<Boolean> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = getContentValuesFromSubject(subject);

        try {
            long rowCount = sqLiteDatabase.update(TABLE_SUBJECT, contentValues,
                    SUBJECT_ID + " =? ", new String[]{String.valueOf(subject.getId())});

            if(rowCount > 0) {
                response.onSuccess(true);
            } else {
                response.onFailure("No subject is updated at all");
            }

        } catch (Exception e){
            response.onFailure(e.getMessage());
        } finally {
            sqLiteDatabase.close();
        }
    }

    @Override
    public void deleteSubject(int subjectId, QueryResponse<Boolean> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            long rowCount = sqLiteDatabase.delete(TABLE_SUBJECT,
                    SUBJECT_ID + " =? ", new String[]{String.valueOf(subjectId)});

            if(rowCount > 0) {
                response.onSuccess(true);
            } else {
                response.onFailure("No subject is deleted at all");
            }

        } catch (Exception e){
            response.onFailure(e.getMessage());
        } finally {
            sqLiteDatabase.close();
        }
    }

    private Subject getSubjectFromCursor(Cursor cursor) {
        int id = getColumnValue(cursor, SUBJECT_ID, -1);
        String subjectName = getColumnValue(cursor, SUBJECT_NAME, "");
        int subjectCode = getColumnValue(cursor, SUBJECT_CODE, -1);
        double subjectCredit = getColumnValue(cursor, SUBJECT_CREDIT, 0.0);

        return new Subject(id, subjectName, subjectCode, subjectCredit);
    }

    private int getColumnValue(Cursor cursor, String columnName, int defaultValue) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1) {
            return cursor.getInt(columnIndex);
        } else {
            return defaultValue;
        }
    }

    private double getColumnValue(Cursor cursor, String columnName, double defaultValue) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1) {
            return cursor.getDouble(columnIndex);
        } else {
            return defaultValue;
        }
    }

    private String getColumnValue(Cursor cursor, String columnName, String defaultValue) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1) {
            return cursor.getString(columnIndex);
        } else {
            return defaultValue;
        }
    }

    private ContentValues getContentValuesFromSubject(Subject subject) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(SUBJECT_NAME, subject.getName());
        contentValues.put(SUBJECT_CODE, subject.getCode());
        contentValues.put(SUBJECT_CREDIT, subject.getCredit());

        return contentValues;
    }
}
