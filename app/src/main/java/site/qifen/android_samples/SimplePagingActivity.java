package site.qifen.android_samples;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;

public class SimplePagingActivity extends AppCompatActivity {

    LiveData<PagedList<User>> allUserList;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_simple_paging);

        RecyclerView recyclerView = binding.getRoot().findViewById(R.id.recyclerView);




        UserDao userDao = MyDb.instance().userDao();

        SimplePagingAdapter simplePagingAdapter = new SimplePagingAdapter();

        recyclerView.setAdapter(simplePagingAdapter);

        allUserList = new LivePagedListBuilder<>(userDao.allUser(), 10).build();


        allUserList.observe(this, new Observer<PagedList<User>>() {
            @Override
            public void onChanged(PagedList<User> users) {
                simplePagingAdapter.submitList(users);

                users.addWeakCallback(null, new PagedList.Callback() {
                    @Override
                    public void onChanged(int position, int count) {

                    }

                    @Override
                    public void onInserted(int position, int count) {

                    }

                    @Override
                    public void onRemoved(int position, int count) {

                    }
                });

            }
        });



        binding.getRoot().findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.getRoot().findViewById(R.id.addDataBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < 1000; i++) {
                    MyDb.instance().userDao().insertUser(new User("name"+i,"say"+i));
                }
            }
        });



    }
}