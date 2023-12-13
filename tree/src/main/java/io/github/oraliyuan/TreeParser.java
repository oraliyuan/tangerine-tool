package io.github.oraliyuan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TreeParser {

    /**
     * 刻画树转list
     */
    public <T> List<T> list2Tree(List<T> entityList,
                                 String defaultParentId,
                                 Function<T, String> getParentBehavior,
                                 Function<T, String> getIdBehavior,
                                 BiConsumer<T, List<T>> setChildrenBehavior) {
        Map<String, List<T>> parentMapping = entityList.stream().collect(Collectors.groupingBy(getParentBehavior));
        entityList.forEach(node -> setChildrenBehavior.accept(node, parentMapping.get(getIdBehavior.apply(node))));
        return entityList.stream().filter(e -> {
            if (defaultParentId == null) {
                return getParentBehavior.apply(e) == null;
            } else {
                return defaultParentId.equals(getParentBehavior.apply(e));
            }
        }).toList();
    }

    /**
     * 刻画树转list
     */
    public <T> List<T> treeList2List(List<T> rootList, Function<T, List<T>> getChildrenBehavior) {
        List<T> list = new ArrayList<>();
        rootList.forEach(node -> {
            list.add(node);
            List<T> children = getChildrenBehavior.apply(node);
            if (children != null && !children.isEmpty()) {
                list.addAll(treeList2List(children, getChildrenBehavior));
            }
        });
        return list;
    }

    /**
     * 单个刻画树转list
     * @param root
     * @param getChildrenBehavior
     * @return
     * @param <T>
     */
    public <T> List<T> singleTree2List(T root, Function<T, List<T>> getChildrenBehavior) {
        List<T> list = new ArrayList<>();
        list.add(root);
        List<T> children = getChildrenBehavior.apply(root);
        if (children != null && !children.isEmpty()) {
            list.addAll(treeList2List(children, getChildrenBehavior));
        }
        return list;
    }

    /**
     * 获取树的某一层节点
     */
    public <T> List<T> getLevelNodes(T treeRoot, Integer level,
                                     Function<T, Integer> getLevelBehavior,
                                     Function<T, List<T>> getChildrenBehavior) {
        List<T> nodes = new ArrayList<>();
        walkAndApply(treeRoot, 0, (T node) -> {
            if (level.equals(Math.abs(getLevelBehavior.apply(node)))) {
                nodes.add(node);
            }
        }, getChildrenBehavior);
        return nodes;
    }

    /**
     * 获取树的叶子节点
     */
    public <T> List<T> getLeafNodes(T treeRoot, Function<T, List<T>> getChildrenBehavior) {
        List<T> nodes = new ArrayList<>();
        walkAndApply(treeRoot, 0, (T node) -> {
            if (getChildrenBehavior.apply(node) == null || getChildrenBehavior.apply(node).isEmpty()) {
                nodes.add(node);
            }
        }, getChildrenBehavior);
        return nodes;
    }

    private <T> void walkAndApply(T node, Integer level, Consumer<T> consumer, Function<T, List<T>> getChildrenBehavior) {
        if (node != null) {
            consumer.accept(node);
            if (getChildrenBehavior.apply(node) != null) {
                getChildrenBehavior.apply(node).forEach(child -> {
                    walkAndApply(child, level + 1, consumer, getChildrenBehavior);
                });
            }
        }
    }

}
