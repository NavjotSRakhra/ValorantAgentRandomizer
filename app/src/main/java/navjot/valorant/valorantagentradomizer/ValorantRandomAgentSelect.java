package navjot.valorant.valorantagentradomizer;

import java.util.Random;

import navjot.valorant.valorantagentradomizer.valorant.AgentFlag;


public class ValorantRandomAgentSelect {
    private final static Random rand = new Random();

    public static int getAgentCode(AgentFlag flag) {
        int checkedAgentCount = flag.getCheckedCount();
        int randAgent = getRandom(checkedAgentCount);
        int code = 0;
        for (int i = 0; i < randAgent; i++) {
            code = flag.getAgentCode(flag.getNextCheckedAgent());
        }
        return code;
    }

    public static String getAgent(AgentFlag flag) {
        int checkedAgentCount = flag.getCheckedCount();
        int randAgent = getRandom(checkedAgentCount);
        String str = "";
        for (int i = 0; i <= randAgent; i++) {
            str = flag.getNextCheckedAgent();
        }
        return str;
    }

    private static int getRandom(int n) {
        n = n <= 0 ? 1 : n;
        return (rand.nextInt(n));
    }
}