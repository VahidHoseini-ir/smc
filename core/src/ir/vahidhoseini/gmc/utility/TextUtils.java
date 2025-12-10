package ir.vahidhoseini.gmc.utility;


public class TextUtils
{
    private TextUtils()
    {

    }

    public static boolean isEmpty(CharSequence text)
    {
        return text == null || text.length() == 0;
    }
}
