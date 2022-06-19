package navjot.valorant.valorantagentradomizer;

import android.app.Activity;
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

        scaleUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_down);

        SELECTED = ResourcesCompat.getColor(getResources(), R.color.selected, null);
        UNSELECTED = ResourcesCompat.getColor(getResources(), R.color.unselected, null);

        if (savedInstanceState != null) {
            restoreRecyclerView(savedInstanceState);
        } else {
            initializeAgentFlagsAndRecyclerView();
        }

        findViewById(R.id.generate).setOnClickListener(this::generateButtonListener);
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
        scaleUp = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_up);
        scaleDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.scale_down);

        SELECTED = ResourcesCompat.getColor(getResources(), R.color.selected, null);
        UNSELECTED = ResourcesCompat.getColor(getResources(), R.color.unselected, null);

        restoreRecyclerView(savedInstanceState);

        super.onRestoreInstanceState(savedInstanceState);
    }

    private void restoreRecyclerView(@NonNull Bundle savedInstanceState) {
        agentFlags = (AgentFlag) savedInstanceState.getSerializable("agentFlags");
        agentDataList = (ArrayList<AgentData>) savedInstanceState.getSerializable("agentDataList");
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        modernAgentAdapter = new ModernAgentAdapter(agentDataList, scaleUp, scaleDown);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(modernAgentAdapter);
        modernAgentAdapter.notifyDataSetChanged();
        recyclerView.scrollTo(0, savedInstanceState.getInt("positionX"));
    }

    private void initializeAgentFlagsAndRecyclerView() {
        modernAgentAdapter = new ModernAgentAdapter(agentDataList, scaleUp, scaleDown);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(modernAgentAdapter);
        String[] array = getResources().getStringArray(R.array.agents);
        agentFlags = new AgentFlag(array);
        int c = 0;
        for (String s : array) {
            agentDataList.add(new AgentData(s, SELECTED));
            modernAgentAdapter.notifyItemInserted(c++);
        }
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

    public static AgentFlag getAgentFlags() {
        return agentFlags;
    }
}