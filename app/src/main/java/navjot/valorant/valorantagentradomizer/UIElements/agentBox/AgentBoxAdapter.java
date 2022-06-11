package navjot.valorant.valorantagentradomizer.UIElements.agentBox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import navjot.valorant.valorantagentradomizer.R;

@Deprecated
public class AgentBoxAdapter extends RecyclerView.Adapter<AgentBox> {
    private List<AgentBoxData> localDataset;

    public AgentBoxAdapter(List<AgentBoxData> dataset) {
        localDataset = dataset;
    }

    @NonNull
    @Override
    public AgentBox onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.agent_box_layout, parent, false);
        return (new AgentBox(view));
    }

    @Override
    public void onBindViewHolder(@NonNull AgentBox holder, int position) {
        holder.getAgentName().setText(localDataset.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return localDataset.size();
    }
}
