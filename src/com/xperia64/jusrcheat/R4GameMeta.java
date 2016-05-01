package com.xperia64.jusrcheat;

/**
 * Created by xperia64 on 4/14/16.
 */
public class R4GameMeta
{
    public final String title;
    public int realPosition;
    public R4GameMeta(String title, String gameId, int realPosition)
    {
        this.title = String.format("%s (%s)", title, gameId);
        this.realPosition = realPosition;
    }
    public R4GameMeta(String total, int realPosition)
    {
        this.title = total;
        this.realPosition = realPosition;
    }
    public void pushDown()
    {
        realPosition--;
    }
}