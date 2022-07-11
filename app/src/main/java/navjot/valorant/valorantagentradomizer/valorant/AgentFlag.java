package navjot.valorant.valorantagentradomizer.valorant;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Arrays;

public class AgentFlag implements Serializable {
    private static int CURRENT_AGENT_COUNT;
    private static String[] AGENTS;
    private final boolean[] agentsChecked;
    private int curIndex;

    public AgentFlag(String[] agents) {
        AGENTS = new String[agents.length];
        System.arraycopy(agents, 0, AGENTS, 0, AGENTS.length);
        CURRENT_AGENT_COUNT = agents.length;
        agentsChecked = new boolean[CURRENT_AGENT_COUNT];
        curIndex = 0;
        // All Agents are selected by default
        Arrays.fill(agentsChecked, true);
    }

//    public AgentFlag(boolean[] agents) {
//        this();
//        if (agents.length >= CURRENT_AGENT_COUNT)
//            throw new IllegalArgumentException("Length of array greater than acceptable length: " + CURRENT_AGENT_COUNT);
//        System.arraycopy(agents, 0, agentsChecked, 0, agents.length);
//    }

    public int getCheckedCount() {
        curIndex = 0;
        int c = 0;
        for (boolean agent : agentsChecked) {
            if (agent) c++;
        }
        return c;
    }

    public String getNextCheckedAgent() {
        if (curIndex >= CURRENT_AGENT_COUNT)
            curIndex = 0;
        while (curIndex < CURRENT_AGENT_COUNT) {
            if (agentsChecked[curIndex++])
                return AGENTS[curIndex - 1];
        }
        curIndex = 0;
        return AGENTS[curIndex++];
    }

    public String getAgent(int i) {
        if (i >= CURRENT_AGENT_COUNT)
            throw new IllegalArgumentException("Index greater than acceptable length: " + CURRENT_AGENT_COUNT);
        return AGENTS[i];
    }

    public int getAgentCode(String agent) {
        for (int i = 0; i < AGENTS.length; i++) {
            if (AGENTS[i].equals(agent))
                return i;
        }
        return -1;
    }

    public void invertAgent(String agent) {
        int i = getAgentCode(agent);
        if (i == -1) {
            System.out.println(agent);
            return;
        }
        agentsChecked[i] = !agentsChecked[i];
    }

    public boolean getAgentState(String agent) {
        int code = getAgentCode(agent);
        if (code == -1) {
            System.out.println(agent);
            return agentsChecked[0];
        }
        return agentsChecked[code];
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder finStr = new StringBuilder();
        finStr.append('<');
        for (int i = 0; i < AGENTS.length; i++) {
            finStr.append("[");
            finStr.append(AGENTS[i]);
            finStr.append(":");
            finStr.append(agentsChecked[i]);
            finStr.append("]");
        }
        finStr.append('>');
        return finStr.toString();
    }
}