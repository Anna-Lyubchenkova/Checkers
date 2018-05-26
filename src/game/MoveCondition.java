package game;

public class MoveCondition {
    private MoveResult moveResult;

    public MoveResult getMoveResult() {
        return moveResult;
    }
    private Checker checker;
    public Checker getChecker() {
        return checker;
    }
    public MoveCondition(MoveResult moveResult){
        this(moveResult,null);
}
    public MoveCondition(MoveResult moveResult, Checker checker){
        this.moveResult=moveResult;
        this.checker=checker;
    }
    public enum MoveResult{
        NONE, NORMAL,KILL()
    }
}
