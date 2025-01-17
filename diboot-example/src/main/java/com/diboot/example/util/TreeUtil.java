package com.diboot.example.util;

import com.diboot.core.util.V;
import com.diboot.example.config.Cons;
import com.diboot.example.entity.Tree;
import com.diboot.example.entity.TreeIcon;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/*
* 树结构工具
* */
public class TreeUtil {

    public static <T> Tree getTree(T entity, String toGetTitle, String toGetKey, String toGetValue, String toGetChildren, boolean hasIcon) throws Exception {
        if(V.isEmpty(entity)){
            return null;
        }
        Class clazz = entity.getClass();
        String className = clazz.getSimpleName();
        String title = null, key = null, value = null;
        List<T> children = null;
        List<Tree> treeChildren = new ArrayList<>();

        if(V.notEmpty(toGetTitle)){
            Method toTitleMthod =clazz.getMethod(toGetTitle);
            title = String.valueOf(toTitleMthod.invoke(entity));
        }
        if(V.notEmpty(toGetKey)){
            Method toKeyMthod =clazz.getMethod(toGetKey);
            key = String.valueOf(toKeyMthod.invoke(entity));
        }
        if(V.notEmpty(toGetValue)){
            Method toValueMthod =clazz.getMethod(toGetValue);
            value = String.valueOf(toValueMthod.invoke(entity));
        }
        if(V.notEmpty(toGetChildren)){
            Method toChildrenMthod =clazz.getMethod(toGetChildren);
            children =  (List<T>)toChildrenMthod.invoke(entity);
        }

        if(V.notEmpty(children)){
            for(T child : children){
                Tree tree = getTree(child, toGetTitle, toGetKey, toGetValue, toGetChildren, hasIcon);
                treeChildren.add(tree);
            }
        }

        Tree tree = null;
        if(V.notEmpty(title)){
            tree = new Tree();
            tree.setTitle(title);
            tree.setKey(key);
            tree.setValue(value);
            TreeIcon treeIcon = new TreeIcon();
            if(V.notEmpty(treeChildren) && treeChildren.size() > 0){
                if(hasIcon){
                    treeIcon.setIcon(Cons.ICON.get(className).get(Cons.TREE_ICON_LEVEL.ONE.name()));
                    tree.setSlots(treeIcon);
                }
                tree.setChildren(treeChildren);
            }else{
                if(hasIcon){
                    treeIcon.setIcon(Cons.ICON.get(className).get(Cons.TREE_ICON_LEVEL.TWO.name()));
                    tree.setSlots(treeIcon);
                }
            }

        }

        return tree;
    }

    public static <T> List<Tree> getTreeList(List<T> entityList, String toGetTitle, String toGetKey, String toGetValue, String toGetChildren, boolean hasIcon) throws Exception {
        if(V.isEmpty(entityList)){
            return null;
        }
        List<Tree> treeList = new ArrayList<>();
        for(T entity : entityList){
            Tree tree = getTree(entity, toGetTitle, toGetKey, toGetValue, toGetChildren, hasIcon);
            if(V.notEmpty(tree)){
                treeList.add(tree);
            }
        }
        return treeList;
    }

}
