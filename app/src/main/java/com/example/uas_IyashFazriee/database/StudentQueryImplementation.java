package com.example.uas_IyashFazriee.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.uas_IyashFazriee.model.Student;
import java.util.ArrayList;
import java.util.List;

import static com.example.uas_IyashFazriee.util.Constants.*;

public class StudentQueryImplementation implements QueryContract.StudentQuery {

    private DatabaseHelper databaseHelper = DatabaseHelper.getInstance();

    @Override
    public void createStudent(Student student, QueryResponse<Boolean> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = getContentValuesForStudent(student);

        try {
            long id = sqLiteDatabase.insertOrThrow(TABLE_STUDENT, null, contentValues);
            if (id > 0) {
                response.onSuccess(true);
                student.setId((int) id);
            } else {
                response.onFailure("Failed to create student. Unknown Reason!");
            }
        } catch (SQLiteException e) {
            response.onFailure(e.getMessage());
        } finally {
            sqLiteDatabase.close();
        }
    }

    @Override
    public void readStudent(int studentId, QueryResponse<Student> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.query(TABLE_STUDENT, null,
                    STUDENT_ID + " =? ", new String[]{String.valueOf(studentId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                Student student = getStudentFromCursor(cursor);
                response.onSuccess(student);
            } else {
                response.onFailure("Student not found with this ID in database");
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
    public void readAllStudent(QueryResponse<List<Student>> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        List<Student> studentList = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.query(TABLE_STUDENT, null, null, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Student student = getStudentFromCursor(cursor);
                    studentList.add(student);
                } while (cursor.moveToNext());

                response.onSuccess(studentList);
            } else {
                response.onFailure("There are no student in database");
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
    public void updateStudent(Student student, QueryResponse<Boolean> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues contentValues = getContentValuesForStudent(student);

        try {
            long rowCount = sqLiteDatabase.update(TABLE_STUDENT, contentValues,
                    STUDENT_ID + " =? ", new String[]{String.valueOf(student.getId())});
            if (rowCount > 0) {
                response.onSuccess(true);
            } else {
                response.onFailure("No data is updated at all");
            }
        } catch (Exception e) {
            response.onFailure(e.getMessage());
        } finally {
            sqLiteDatabase.close();
        }
    }

    @Override
    public void deleteStudent(int studentId, QueryResponse<Boolean> response) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        try {
            long rowCount = sqLiteDatabase.delete(TABLE_STUDENT, STUDENT_ID + " =? ",
                    new String[]{String.valueOf(studentId)});

            if (rowCount > 0) {
                response.onSuccess(true);
            } else {
                response.onFailure("Failed to delete student. Unknown reason");
            }
        } catch (Exception e) {
            response.onFailure(e.getMessage());
        } finally {
            sqLiteDatabase.close();
        }
    }

    private ContentValues getContentValuesForStudent(Student student) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(STUDENT_NAME, student.getName());
        contentValues.put(STUDENT_REGISTRATION_NUM, student.getRegistrationNumber());
        contentValues.put(STUDENT_PHONE, student.getPhone());
        contentValues.put(STUDENT_EMAIL, student.getEmail());

        return contentValues;
    }

    private Student getStudentFromCursor(Cursor cursor) {
        int id = getColumnValue(cursor, STUDENT_ID, -1);
        String name = getColumnValue(cursor, STUDENT_NAME, "");
        int regNum = getColumnValue(cursor, STUDENT_REGISTRATION_NUM, -1);
        String phone = getColumnValue(cursor, STUDENT_PHONE, "");
        String email = getColumnValue(cursor, STUDENT_EMAIL, "");

        return new Student(id, name, regNum, phone, email);
    }

    private int getColumnValue(Cursor cursor, String columnName, int defaultValue) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1) {
            return cursor.getInt(columnIndex);
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