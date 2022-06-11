package navjot.valorant.valorantagentradomizer.UIElements.modernAgentHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import navjot.valorant.valorantagentradomizer.MainActivity;
import navjot.valorant.valorantagentradomizer.R;
import navjot.valorant.valorantagentradomizer.valorant.AgentFlag;

public class ModernAgentAdapter extends RecyclerView.Adapter<AgentDataViewHolder> {
    private final List<AgentData> agentDataList;

    public ModernAgentAdapter(List<AgentData> agentDataList) {
        this.agentDataList = agentDataList;
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

//    @Override
//    public void onClick(View v) {
//        AgentFlag agentFlags = MainActivity.getAgentFlags();
//        TextView view = (TextView) ((CardView) ((RelativeLayout) ((CardView) ((RelativeLayout) v).getChildAt(0)).getChildAt(0)).getChildAt(0)).getChildAt(0);
//        String agent = view.getText().toString();
//        int agentCode = agentFlags.getAgentCode(agent);
//        agentFlags.invertAgent(agent);
//        boolean isSelected = agentFlags.getAgentState(agent);
//        if (isSelected) {
//            ((CardView) ((RelativeLayout) v).getChildAt(0)).setCardBackgroundColor(Color.parseColor("#FF0000"));
//        } else {
//            ((CardView) ((RelativeLayout) v).getChildAt(0)).setCardBackgroundColor(Color.parseColor("#FFFFFF"));
//        }
//        System.out.println(agentFlags);
//    }
}
