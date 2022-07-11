package navjot.valorant.valorantagentradomizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Configuration;
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

import java.util.ArrayList;

import navjot.valorant.valorantagentradomizer.UIElements.modernAgentHolder.AgentData;
import navjot.valorant.valorantagentradomizer.UIElements.modernAgentHolder.ModernAgentAdapter;
import navjot.valorant.valorantagentradomizer.valorant.AgentFlag;

public class MainActivity extends Activity {

    public static int SELECTED, UNSELECTED;

    private ArrayList<AgentData> agentDataList = new ArrayList<>();
    private ModernAgentAdapter modernAgentAdapter;
    private static AgentFlag agentFlags;
    private Animation scaleUp, scaleDown;

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
