/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geeosp.pathtracing;

import geeosp.pathtracing.scene.RenderScene;

/**
 *
 * @author geeo
 */
public abstract class RenderAlgorithm {
   
    public abstract  double[] calulatePixel(int i, int y, RenderScene scene);
    
}
