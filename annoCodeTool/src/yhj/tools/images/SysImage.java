package yhj.tools.images;

import java.awt.Image;
import javax.swing.ImageIcon;

public class SysImage
{
  public static Image getImage(String imageName)
  {
    return new ImageIcon(SysImage.class.getResource(imageName)).getImage();
  }

  public static ImageIcon getImageIcon(String imageName)
  {
    return new ImageIcon(SysImage.class.getResource(imageName));
  }
}
