package navjot.valorant.valorantagentradomizer.UIElements.agentBox;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import navjot.valorant.valorantagentradomizer.R;

@Deprecated
public class AgentBox extends RecyclerView.ViewHolder {
    private final TextView agentName;

    public AgentBox(View view) {
        super(view);

        agentName = view.findViewById(R.id.agentName);
    }

    public TextView getAgentName() {
        return agentName;
    }
}
