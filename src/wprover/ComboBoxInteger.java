package wprover;

import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JComboBox;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: 2005-3-4
 * Time: 14:25:06
 * To change this template use File | Settings | File Templates.
 */
public class ComboBoxInteger extends JComboBox<Integer> {
    /**
	 * 
	 */
	private static final long serialVersionUID = -7704016026647356301L;
	private static ArrayList<ComboBoxInteger> instanceList = new ArrayList<ComboBoxInteger>();
    int defaultindex = 0;

    public static ComboBoxInteger CreateAInstance() {
        Integer[] intArray = new Integer[DrawData.getColorCounter() + 1];
        for (int i = 0; i <= DrawData.getColorCounter(); i++) {
            intArray[i] = new Integer(i);
        }
        ComboBoxInteger cb = new ComboBoxInteger(intArray);

        cb.setMaximumRowCount(30);
        cb.setPreferredSize(new Dimension(40, 20));
        PanelColorComboRender render = new PanelColorComboRender(0, 100, 20);
        render.setPreferredSize(new Dimension(40, 20));
        cb.setRenderer(render);
        instanceList.add(cb);
        return cb;
    }

    private ComboBoxInteger(final Integer items[]) {
        super(items);
    }

    @Override
    public void setSelectedIndex(int index) {
        ((PanelColorComboRender) super.getRenderer()).index = index;
        super.setSelectedIndex(index);
    }

    public void setDefaultIndex(int index) {
        defaultindex = index;
    }

    public static void reGenerateAll() {
        for (ComboBoxInteger cb : instanceList) {
        	int co = DrawData.getColorCounter();
            int n = cb.getItemCount();

            if (co >= n)
                for (int j = n; j <= co; j++)
                    cb.addItem(new Integer(j));
        }
    }

    public static void resetAll() {
        DrawData.reset();

        for (ComboBoxInteger cb : instanceList) {
            cb.setSelectedIndex(cb.defaultindex);
            int num = DrawData.getColorCounter();
            for(int j = num+1; j < cb.getItemCount(); j++)
                cb.removeItemAt(j);
        }
    }

}
