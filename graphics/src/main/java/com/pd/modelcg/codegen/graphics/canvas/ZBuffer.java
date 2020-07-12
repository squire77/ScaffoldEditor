package com.pd.modelcg.codegen.graphics.canvas;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ZBuffer<T extends IDrawable>
{
    private List<T> objects = new ArrayList<>();

    public void clear()
    {
        objects = new ArrayList<>();
    }

    public void apply(Consumer<T> consumer)
    {
        objects.forEach(consumer);
    }

    public void drawAllBackToFront(Graphics2D g)
    {
        for (T object: objects) {
            object.draw(g);
        }
    }

    public boolean remove(T object) { return objects.remove(object); }

    public void add(T object) { objects.add(object); }

    public List<T> getAllAtPosition(int x, int y)
    {
        return objects.stream()
                .filter((object) -> object.contains(x, y))
                .collect(Collectors.toList());
    }

    public T getFrontMostAtPosition(int x, int y)
    {
        // select objects from the top down in the z-buffer (in case they overlap)
        if(objects.size() > 0) {
            for(int i=objects.size() - 1; i >= 0; i--) {
                T next = objects.get(i);
                if( next.contains(x, y))
                    return next;
            }
        }

        return null;
    }
}
