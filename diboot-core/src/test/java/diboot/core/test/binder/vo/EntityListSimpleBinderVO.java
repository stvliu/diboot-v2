package diboot.core.test.binder.vo;

import com.diboot.core.binding.annotation.BindEntityList;
import diboot.core.test.binder.entity.Department;

import java.util.List;

/**
 * @author Mazhicheng
 * @version v2.0
 * @date 2019/1/5
 */
public class EntityListSimpleBinderVO extends Department {
    private static final long serialVersionUID = -362116388664907913L;

    // 直接关联多个Entity
    @BindEntityList(entity = Department.class, condition = "this.id=parent_id")
    private List<Department> children;

    public List<Department> getChildren() {
        return children;
    }

    public void setChildren(List<Department> children) {
        this.children = children;
    }
}