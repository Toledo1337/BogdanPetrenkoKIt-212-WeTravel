package ua.pkk.wetravel.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ua.pkk.wetravel.R;
import ua.pkk.wetravel.utils.Keys;
import ua.pkk.wetravel.utils.User;

//TODO Fix problems with screen orientation Priority -> HIGH
public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkSharedPreferences(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    private void checkSharedPreferences(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("isCreated")) return;
        SharedPreferences sharedPreferences = this.getSharedPreferences("user_data", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("userID", "");
        if (id.isEmpty()) {
            return;
        }
        User.getInstance().setId(id);
        NavOptions options = new NavOptions.Builder().setPopUpTo(R.id.loginPageFragment, true).build();
        if(!Keys.isNewDesign()){
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.mainFragment, null, options);
        } else {
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.mainFragment2, null, options);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("isCreated", true);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            signInAnonymously();
        }
        super.onStart();
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnFailureListener(this,
                        exception -> Log.e("TAG", "signInAnonymously:FAILURE", exception));
    }

    @Override
    public void onBackPressed() {
        //TODO Do something with this
        return;
    }
}