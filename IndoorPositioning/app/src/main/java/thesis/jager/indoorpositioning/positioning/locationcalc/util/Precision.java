package thesis.jager.indoorpositioning.positioning.locationcalc.util;

/**
 * Created by Jager on 2016.02.09..
 */
public enum Precision
{
       NoTag(0),
       OneTag (1),
       TwoTag (2),
       ThreeOrMoreTag(3);

       private int num;

       Precision(int num)
       {
              this.num = num;
       }

       public int num()
       {
              return num;
       }

       public static Precision getEnum(int value)
       {
              switch (value)
              {
                     case 3: return ThreeOrMoreTag;
                     case 2: return TwoTag;
                     case 1: return OneTag;
                     case 0:
                     default:
                            return NoTag;
              }
       }


}
