package navjot.valorant.valorantagentradomizer.UIElements.modernAgentHolder;

import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import navjot.valorant.valorantagentradomizer.MainActivity;
import navjot.valorant.valorantagentradomizer.R;
import navjot.valorant.valorantagentradomizer.valorant.AgentFlag;

public class AgentDataViewHolder extends RecyclerView.ViewHolder{
    private final TextView agentName;
    private final CardView select;

    public AgentDataViewHolder(@NonNull View itemView) {
        super(itemView);
        agentName = itemView.findViewById(R.id.agentNameModern);
        select = itemView.findViewById(R.id.select);
    }

    public TextView getAgentNameTextField() {
        return agentName;
    }

    public CardView getSelect() {
        return select;
    }
}
