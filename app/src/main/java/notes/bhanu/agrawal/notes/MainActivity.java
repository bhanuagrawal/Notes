package notes.bhanu.agrawal.notes;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import androidx.navigation.fragment.NavHostFragment;
import notes.bhanu.agrawal.notes.ui.FragmentInteraction;

public class MainActivity extends FragmentActivity implements FragmentInteraction {

    private NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.my_nav_host_fragment);

    }


    @Override
    public void navigateTo(int resId, Bundle bundle) {
        if(bundle!=null){
            navHostFragment.getNavController().navigate(resId, bundle);
        }
        else{
            navHostFragment.getNavController().navigate(resId);

        }
    }
}
