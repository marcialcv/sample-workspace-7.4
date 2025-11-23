package override;

import com.liferay.petra.lang.HashUtil;

import java.io.Serializable;
import java.util.Objects;


public class NavItemsCacheKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private final long _userId;
    private final long _groupId;

    public NavItemsCacheKey(long userId, long groupId) {
        _userId = userId;
        _groupId = groupId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof NavItemsCacheKey)) {
            return false;
        }

        NavItemsCacheKey other = (NavItemsCacheKey) object;

        return (_userId == other._userId) &&
               (_groupId == other._groupId);
    }

    @Override
    public int hashCode() {
        int hash = HashUtil.hash(0, _userId);
        return HashUtil.hash(hash, _groupId);
    }
}
