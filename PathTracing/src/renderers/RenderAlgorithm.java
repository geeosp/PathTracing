/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package renderers;

import geeosp.pathtracing.scene.RenderScene;

/**
 *
 * @author geeo
 */
public abstract class RenderAlgorithm {

    public abstract void set(RenderScene renderScene);

    public abstract double[] calulatePixel(int i, int y, RenderScene scene);

}
