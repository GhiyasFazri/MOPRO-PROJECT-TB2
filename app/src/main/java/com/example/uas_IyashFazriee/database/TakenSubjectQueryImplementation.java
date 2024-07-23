package com.example.uas_IyashFazriee.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.example.uas_IyashFazriee.model.TakenSubject;
import com.example.uas_IyashFazriee.model.Subject;

import java.util.ArrayList;
import java.util.List;

import static com.example.uas_IyashFazriee.util.Constants.STUDENT_ID_FK;
import static com.example.uas_IyashFazriee.util.Constants.SUBJECT_CREDIT;
import static com.example.uas_IyashFazriee.util.Constants.SUBJECT_ID;
import static com.example.uas_IyashFazriee.util.Constants.SUBJECT_ID_FK;
import static com.example.uas_IyashFazriee.util.Constants.SUBJECT_NAME;
import static com.example.uas_IyashFazriee.util.Constants.TABLE_STUDENT_SUBJECT;
import static com.example.uas_IyashFazriee.util.Constants.SUBJECT_CODE;

public class TakenSubjectQueryImplementation implements QueryContract.TakenSubjectQuery {

    private DatabaseHelper databaseHelper = DatabaseHelper.getInstance();

    @Override
    public void createTakenSubject(int studentId, int subjectId, QueryResponse<Boolean> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(STUDENT_ID_FK, studentId);
        contentValues.put(SUBJECT_ID_FK, subjectId);

        try {
            long rowCount = sqLiteDatabase.insertOrThrow(TABLE_STUDENT_SUBJECT, null, contentValues);

            if (rowCount > 0) {
                response.onSuccess(true);
            } else {
                response.onFailure("Subject assign failed");
            }

        } catch (SQLiteException e) {
            response.onFailure(e.getMessage());
        } finally {
            sqLiteDatabase.close();
        }
    }

    @Override
    public void readAllTakenSubjectByStudentId(int studentId, QueryResponse<List<Subject>> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        String QUERY = "SELECT * FROM subject as s JOIN student_subject as ss ON s._id = ss.subject_id WHERE ss.student_id = " + studentId;
        Cursor cursor = null;
        try {
            List<Subject> subjectList = new ArrayList<>();
            cursor = sqLiteDatabase.rawQuery(QUERY, null);

            if (cursor.moveToFirst()) {
                do {
                    int id = getColumnValue(cursor, SUBJECT_ID, -1);
                    String subjectName = getColumnValue(cursor, SUBJECT_NAME, "");
                    int subjectCode = getColumnValue(cursor, SUBJECT_CODE, -1);
                    double subjectCredit = getColumnValue(cursor, SUBJECT_CREDIT, 0.0);

                    Subject subject = new Subject(id, subjectName, subjectCode, subjectCredit);
                    subjectList.add(subject);

                } while (cursor.moveToNext());

                response.onSuccess(subjectList);
            } else {
                response.onFailure("There are no subjects assigned to this student");
            }

        } catch (Exception e) {
            response.onFailure(e.getMessage());
        } finally {
            sqLiteDatabase.close();
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void readAllSubjectWithTakenStatus(int studentId, QueryResponse<List<TakenSubject>> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        String QUERY = "SELECT s._id, s.name, s.code, s.credit, ss.student_id " +
                "FROM subject as s LEFT JOIN student_subject as ss ON s._id = ss.subject_id " +
                "AND ss.student_id = " + studentId;

        Cursor cursor = null;
        try {
            List<TakenSubject> takenSubjectList = new ArrayList<>();
            cursor = sqLiteDatabase.rawQuery(QUERY, null);

            if (cursor.moveToFirst()) {
                do {
                    int id = getColumnValue(cursor, SUBJECT_ID, -1);
                    String subjectName = getColumnValue(cursor, SUBJECT_NAME, "");
                    int subjectCode = getColumnValue(cursor, SUBJECT_CODE, -1);
                    double subjectCredit = getColumnValue(cursor, SUBJECT_CREDIT, 0.0);

                    boolean isTaken = getColumnValue(cursor, STUDENT_ID_FK, -1) > 0;

                    TakenSubject takenSubject = new TakenSubject(id, subjectName, subjectCode, subjectCredit, isTaken);
                    takenSubjectList.add(takenSubject);

                } while (cursor.moveToNext());

                response.onSuccess(takenSubjectList);
            } else {
                response.onFailure("There are no subjects assigned to this student");
            }

        } catch (Exception e) {
            response.onFailure(e.getMessage());
        } finally {
            sqLiteDatabase.close();
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void deleteTakenSubject(int studentId, int subjectId, QueryResponse<Boolean> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            long rowCount = sqLiteDatabase.delete(TABLE_STUDENT_SUBJECT,
                    STUDENT_ID_FK + " =? AND " + SUBJECT_ID_FK + " =? ",
                    new String[]{String.valueOf(studentId), String.valueOf(subjectId)});

            if (rowCount > 0) {
                response.onSuccess(true);
            } else {
                response.onFailure("Assigned subject deletion failed");
            }

        } catch (Exception e) {
            response.onFailure(e.getMessage());
        } finally {
            sqLiteDatabase.close();
        }
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
}
