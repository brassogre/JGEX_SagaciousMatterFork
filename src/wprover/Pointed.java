package wprover;

import java.util.ArrayList;
import java.util.Collection;

public abstract interface Pointed {
	public abstract boolean isCoincidentWith(Pointed pointedEntity);
    public abstract boolean isCoincidentWith(ArrayList<GEPoint> pList);
    public abstract boolean isFullyCoincidentWith(final ArrayList<GraphicEntity> pList);
    public abstract boolean isCoincidentWith(GEPoint pp);
    public abstract GEPoint getCommonPoints(Pointed pointedEntity, Collection<GEPoint> collectionPoints);
    public abstract GEPoint getCommonPoints(ArrayList<GEPoint> pList, Collection<GEPoint> collectionPoints);
    public abstract GEPoint getCommonPoints(GEPoint pp, Collection<GEPoint> collectionPoints);
}
