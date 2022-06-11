package navjot.valorant.valorantagentradomizer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import navjot.valorant.valorantagentradomizer.UIElements.modernAgentHolder.AgentData;
import navjot.valorant.valorantagentradomizer.UIElements.modernAgentHolder.ModernAgentAdapter;
import navjot.valorant.valorantagentradomizer.valorant.AgentFlag;

public class MainActivity extends Activity {

    public static int SELECTED, UNSELECTED;

    private final List<AgentData> agentDataList = new ArrayList<>();
    private ModernAgentAdapter modernAgentAdapter;
    private static AgentFlag agentFlags;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SELECTED = ResourcesCompat.getColor(getResources(), R.color.selected, null);
        UNSELECTED = ResourcesCompat.getColor(getResources(), R.color.unselected, null);

        initializeAgentFlagsAndRecyclerView();

        findViewById(R.id.generate).setOnClickListener((v) -> generateButtonListener());
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initializeAgentFlagsAndRecyclerView() {
        modernAgentAdapter = new ModernAgentAdapter(agentDataList);
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

    public void generateButtonListener() {
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
