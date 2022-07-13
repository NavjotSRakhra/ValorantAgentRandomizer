package navjot.valorant.valorantagentradomizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

import navjot.valorant.valorantagentradomizer.UIElements.modernAgentHolder.AgentData;
import navjot.valorant.valorantagentradomizer.UIElements.modernAgentHolder.ModernAgentAdapter;
import navjot.valorant.valorantagentradomizer.valorant.AgentFlag;

public class MainActivity extends Activity {

    public static int SELECTED, UNSELECTED;

    private ArrayList<AgentData> agentDataList = new ArrayList<>();
    private ModernAgentAdapter modernAgentAdapter;
    private static AgentFlag agentFlags;
    private Animation scaleUp, scaleDown;
    private static final String buildVersionName = BuildConfig.VERSION_NAME;
    private static final String latestReleaseURL = "https://api.github.com/repos/NavjotSRakhra/ValorantAgentRandomizer/releases/latest";
    private static String latestBuildVersion = "@";
    private static final ExecutorService executorService = Executors.newFixedThreadPool(1);
    private static String latestReleaseResponse = "@";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            restoreAgentFlagsAndRecyclerView(savedInstanceState);
        } else {
            initializeAgentFlagsAndRecyclerView();
        }

        findViewById(R.id.generate).setOnClickListener(this::generateButtonListener);
        findViewById(R.id.helpFloatingActionButton).setOnClickListener(this::helpFloatingActionButtonListener);

        checkUpdates(executorService, this::update);
    }

    private void update(String updateLink) {

    }

    private void checkUpdates(Executor executor, OnUpdatable actionIfUpdatable) {
        executor.execute(() -> {
            latestBuildVersion = getLatestBuildVersion();
            if (isUpdatable()) {
                String updateLink = getUpdateLink();
                actionIfUpdatable.onComplete(updateLink);
            }
        });
    }

    private String getUpdateLink() {
        String temp = latestReleaseResponse.substring(latestReleaseResponse.indexOf("browser_download_url") + ("browser_download_url\": ".length()));
        temp = temp.substring(0, temp.indexOf("\""));
        return temp;
    }

    private boolean isUpdatable() {
        System.out.println(latestBuildVersion + "|" + buildVersionName);
        return !latestBuildVersion.equals(buildVersionName);
    }

    private String getLatestBuildVersion() {
        try {

            if (((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo().isAvailable()) {
                int c = 0;

                URL url = new URL(MainActivity.latestReleaseURL);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                InputStream in = httpsURLConnection.getInputStream();

                StringBuilder stringBuilder = new StringBuilder();

                while (-1 != (c = in.read())) {
                    stringBuilder.appendCodePoint(c);
                }
                in.close();
                httpsURLConnection.disconnect();

                latestReleaseResponse = stringBuilder.toString();
                String tag_name = latestReleaseResponse.substring(latestReleaseResponse.indexOf("tag_name") + ("tag_name\": ".length()));
                return tag_name.substring(1, tag_name.indexOf('\"'));
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return buildVersionName;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable("agentFlags", agentFlags);
        outState.putSerializable("agentDataList", agentDataList);
        outState.putInt("positionX", (findViewById(R.id.recycler_view)).getScrollY());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        restoreAgentFlagsAndRecyclerView(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void restoreAgentFlagsAndRecyclerView(@NonNull Bundle savedInstanceState) {
        agentFlags = (AgentFlag) savedInstanceState.getSerializable("agentFlags");
        //noinspection unchecked
        agentDataList = (ArrayList<AgentData>) savedInstanceState.getSerializable("agentDataList");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        initializeRecyclerViewWithCurrentData();

        modernAgentAdapter.notifyItemRangeChanged(0, agentDataList.size());
        recyclerView.scrollTo(0, savedInstanceState.getInt("positionX"));
    }

    private void initializeAgentFlagsAndRecyclerView() {
        String[] array = getResources().getStringArray(R.array.agents);
        agentFlags = new AgentFlag(array);

        initializeRecyclerViewWithCurrentData();

        int c = 0;
        for (String s : array) {
            agentDataList.add(new AgentData(s, SELECTED));
            modernAgentAdapter.notifyItemInserted(c++);
        }
    }

    private void initializeRecyclerViewWithCurrentData() {

        scaleUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_down);

        SELECTED = ResourcesCompat.getColor(getResources(), R.color.selected, null);
        UNSELECTED = ResourcesCompat.getColor(getResources(), R.color.unselected, null);

        int spanCount = 5;

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            spanCount = 3;
        }
        modernAgentAdapter = new ModernAgentAdapter(agentDataList, scaleUp, scaleDown);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), spanCount);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(modernAgentAdapter);
    }

    public void generateButtonListener(View v) {
        v.startAnimation(scaleUp);
        v.startAnimation(scaleDown);
        new Thread(() -> {
            String agent = ValorantRandomAgentSelect.getAgent(agentFlags);
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), agent, Toast.LENGTH_SHORT).show()
            );
        }).start();
    }

    public void helpFloatingActionButtonListener(View v) {

        v.startAnimation(scaleUp);
        v.startAnimation(scaleDown);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.help).setMessage(R.string.help_dialog_content).setPositiveButton(R.string.ok, null).show();
    }

    public static AgentFlag getAgentFlags() {
        return agentFlags;
    }
}
//Updated java version from java 8 to java 11