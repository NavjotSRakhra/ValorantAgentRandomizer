package navjot.valorant.valorantagentradomizer.UIElements.modernAgentHolder;

import java.io.Serializable;

public class AgentData implements Serializable {
    private String agentName;
    private int backgroundResourceID;

    public AgentData(String name, int backgroundResourceID) {
        agentName = name;
        this.backgroundResourceID = backgroundResourceID;

    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public int getBackgroundResourceID() {
        return backgroundResourceID;
    }

    public void setBackgroundResourceID(int backgroundResourceID) {
        this.backgroundResourceID = backgroundResourceID;
    }
}
