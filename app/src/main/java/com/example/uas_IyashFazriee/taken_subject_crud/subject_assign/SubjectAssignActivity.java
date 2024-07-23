package com.example.uas_IyashFazriee.taken_subject_crud.subject_assign;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.uas_IyashFazriee.R;
import com.example.uas_IyashFazriee.database.*;
import com.example.uas_IyashFazriee.model.TakenSubject;
import static  com.example.uas_IyashFazriee.util.Constants.STUDENT_ID;


import java.util.ArrayList;
import java.util.List;


public class SubjectAssignActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noDataFoundTextView;

    private List<TakenSubject> takenSubjectList = new ArrayList<>();
    private SubjectAssignListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_assign);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        recyclerView = findViewById(R.id.recyclerView);
        noDataFoundTextView = findViewById(R.id.noDataFoundTextView);

        int studentId = getIntent().getIntExtra(STUDENT_ID, -1);

        adapter = new SubjectAssignListAdapter(this, studentId, takenSubjectList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);

        QueryContract.TakenSubjectQuery query = new TakenSubjectQueryImplementation();
        query.readAllSubjectWithTakenStatus(studentId, new QueryResponse<List<TakenSubject>>() {
            @Override
            public void onSuccess(List<TakenSubject> data) {
                recyclerView.setVisibility(View.VISIBLE);
                noDataFoundTextView.setVisibility(View.GONE);

                takenSubjectList.clear();
                takenSubjectList.addAll(data);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String message) {
                recyclerView.setVisibility(View.GONE);
                noDataFoundTextView.setVisibility(View.VISIBLE);
            }
        });

    }

    public void closeActivity(View view) {
        finish();
    }
}
