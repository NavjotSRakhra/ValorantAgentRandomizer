package navjot.valorant.valorantagentradomizer.UIElements.modernAgentHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.util.List;

import navjot.valorant.valorantagentradomizer.MainActivity;
import navjot.valorant.valorantagentradomizer.R;
import navjot.valorant.valorantagentradomizer.valorant.AgentFlag;

public class ModernAgentAdapter extends RecyclerView.Adapter<AgentDataViewHolder> implements Serializable {
    private final List<AgentData> agentDataList;
    private final Animation scaleUp, scaleDown;

    public ModernAgentAdapter(List<AgentData> agentDataList, Animation scaleUp, Animation scaleDown) {
        this.agentDataList = agentDataList;
        this.scaleUp = scaleUp;
        this.scaleDown = scaleDown;
    }

    @NonNull
    @Override
    public AgentDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.modern_agentholder_model, parent, false);
        return new AgentDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgentDataViewHolder holder, int position) {
        holder.getAgentNameTextField().setText(agentDataList.get(position).getAgentName());
        holder.getSelect().setCardBackgroundColor(agentDataList.get(position).getBackgroundResourceID());
        holder.getAgentNameTextField().setOnClickListener(v -> {
            v.startAnimation(scaleUp);
            v.startAnimation(scaleDown);
            AgentFlag agentFlags = MainActivity.getAgentFlags();
            agentFlags.invertAgent(agentFlags.getAgent(position));
            if (agentFlags.getAgentState(agentFlags.getAgent(position))) {
                holder.getSelect().setCardBackgroundColor(MainActivity.SELECTED);
                agentDataList.get(position).setBackgroundResourceID(MainActivity.SELECTED);
            } else {
                holder.getSelect().setCardBackgroundColor(MainActivity.UNSELECTED);
                agentDataList.get(position).setBackgroundResourceID(MainActivity.UNSELECTED);
            }
        });
    }

    @Override
    public int getItemCount() {
        return agentDataList.size();
    }
}
