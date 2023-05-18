package navjot.valorant.valorantagentradomizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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

import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import navjot.valorant.valorantagentradomizer.UIElements.modernAgentHolder.AgentData;
import navjot.valorant.valorantagentradomizer.UIElements.modernAgentHolder.ModernAgentAdapter;
import navjot.valorant.valorantagentradomizer.update.Updatable;
import navjot.valorant.valorantagentradomizer.update.Update;
import navjot.valorant.valorantagentradomizer.valorant.AgentFlag;

public class MainActivity extends Activity {

    public static int SELECTED, UNSELECTED;

    private ArrayList<AgentData> agentDataList = new ArrayList<>();
    private ModernAgentAdapter modernAgentAdapter;
    private static AgentFlag agentFlags;
    private Animation scaleUp, scaleDown;
    private static final String buildVersionName = BuildConfig.VERSION_NAME;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            restoreAgentFlagsAndRecyclerView(savedInstanceState);
        } else {
            initializeAgentFlagsAndRecyclerView();
        }

        initializeListenersAndAnalyticsAndUpdateChecks();
    }

    private void initializeListenersAndAnalyticsAndUpdateChecks() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        findViewById(R.id.generate).setOnClickListener(this::generateButtonListener);
        findViewById(R.id.helpFloatingActionButton).setOnClickListener(this::helpFloatingActionButtonListener);

        Updatable checkUpdatable = new Updatable(this, buildVersionName, "NavjotSRakhra", "ValorantAgentRandomizer");
        checkUpdatable.checkUpdates(executorService, this::newUpdateAvailable);
    }

    private void newUpdateAvailable(String updateLink) {
        SharedPreferences pref = this.getSharedPreferences("download_prompt", Context.MODE_PRIVATE);
        int n = pref.getInt("down", 1);
        System.out.println("download pref");
        System.out.println(n);
        if (n == 0)
            return;
        runOnUiThread(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.new_version_available).setMessage(R.string.download_prompt).setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                Update updater = new Update(this, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString(), updateLink);
                executorService.execute(() -> downloadAndInstall(updater));
            }).setNegativeButton(R.string.no, null).setNeutralButton(R.string.dont_prompt_again, (dialogInterface, i) -> pref.edit().putInt("down", 0).apply()).show();
        });
    }

    private void downloadAndInstall(Update update) {
        String fileName = this.getSharedPreferences("downloadedFileName", Context.MODE_PRIVATE).getString("fileName", "n");
        System.out.println("shared preference");
        System.out.println(fileName);
        if (fileName.equals("n")) {
            update.download();
        }
        try {
            update.install();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            agentFlags = (AgentFlag) savedInstanceState.getSerializable("agentFlags", AgentFlag.class);
        } else {
            agentFlags = (AgentFlag) savedInstanceState.getSerializable("agentFlags");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //noinspection unchecked
            agentDataList = (ArrayList<AgentData>) savedInstanceState.getSerializable("agentDataList", ArrayList.class);
        } else {
            //noinspection unchecked
            agentDataList = (ArrayList<AgentData>) savedInstanceState.getSerializable("agentDataList");
        }
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