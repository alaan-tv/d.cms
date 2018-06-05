package media.dee.dcms.core.services;

import media.dee.dcms.core.db.Record;

public interface ComponentService {
    Record findComponentById(long componentId);
}
