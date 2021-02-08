package site.qifen.android_samples;

import androidx.lifecycle.LiveData;
import androidx.paging.DataSource;
import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.PrimaryKey;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.io.Serializable;
import java.util.List;


@Database(exportSchema = false,version = 1,entities = User.class)
public abstract class MyDb extends RoomDatabase {
    abstract UserDao userDao();

    static synchronized MyDb instance() {
       return Room.databaseBuilder(App.getContext(), MyDb.class, "paging.db").allowMainThreadQueries().build();
    }

}


@Dao
interface UserDao {

    @Insert
    void insertUser(User... users);

    @Query("select * from user")
    DataSource.Factory<Integer,User> allUser();

}


@Entity
class User implements Serializable {
    @PrimaryKey(autoGenerate = true)
    int userId;
    String userName;
    String userSay;

    @Ignore
    public User(int userId, String userName, String userSay) {
        this.userId = userId;
        this.userName = userName;
        this.userSay = userSay;
    }

    public User(String userName, String userSay) {
        this.userName = userName;
        this.userSay = userSay;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserSay() {
        return userSay;
    }

    public void setUserSay(String userSay) {
        this.userSay = userSay;
    }

}