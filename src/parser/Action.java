package parser;

public class Action {
    ShiftReduce act;
    int actNum;

    public Action(String action, String num) throws Exception {
        if (action.equals("shift")) {
            act = ShiftReduce.SHIFT;
        } else if (action.equals("reduce")) {
            act = ShiftReduce.REDUCE;
        } else {
            throw new Exception("unexpected input.");
        }

        actNum = Integer.parseInt(num);
    }

    public ShiftReduce getShiftReduce() {
        return act;
    }

    public int getNum() {
        return actNum;
    }

    @Override
    public String toString() {
        return act.toString().toLowerCase() + " " + actNum;
    }
}
