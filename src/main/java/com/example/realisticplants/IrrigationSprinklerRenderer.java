package com.realisticplants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.phys.Vec3;

public final class IrrigationSprinklerRenderer implements BlockEntityRenderer<IrrigationSprinklerBlockEntity, IrrigationSprinklerRenderer.State> {
    public IrrigationSprinklerRenderer(BlockEntityRendererProvider.Context context) {}
    public static final class State extends BlockEntityRenderState {
        public float angle;
        public boolean active;
    }
    @Override public State createRenderState() { return new State(); }
    @Override public void extractRenderState(IrrigationSprinklerBlockEntity sprinkler, State state, float partialTick,
            Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(sprinkler, state, partialTick, cameraPosition, breakProgress);
        state.angle = sprinkler.getInterpolatedRotation(partialTick);
        state.active = sprinkler.getBlockState().getValue(IrrigationSprinklerBlock.ACTIVE);
    }
    @Override public void submit(State state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState camera) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.69, 0.5);
        collector.submitCustomGeometry(poseStack, RenderTypes.lightning(),
                (pose, buffer) -> renderRotor(pose, buffer, state.angle, state.active));
        poseStack.popPose();
    }
    private static void renderRotor(PoseStack.Pose pose, VertexConsumer buffer, float angleDegrees, boolean active) {
        float rotation=(float)Math.toRadians(angleDegrees), brightness=active?1.0f:0.72f;
        for(int blade=0;blade<4;blade++) {
            float a=rotation+blade*(float)(Math.PI/2.0), c=(float)Math.cos(a), s=(float)Math.sin(a);
            float inner=0.035f, outer=0.29f, wi=0.025f, wo=0.045f;
            emitQuad(pose,buffer,c*inner-s*wi,0,s*inner+c*wi,c*inner+s*wi,0,s*inner-c*wi,
                    c*outer+s*wo,0,s*outer-c*wo,c*outer-s*wo,0,s*outer+c*wo,
                    0.66f*brightness,0.70f*brightness,0.75f*brightness);
        }
        emitQuad(pose,buffer,-0.055f,0.008f,-0.055f,0.055f,0.008f,-0.055f,
                0.055f,0.008f,0.055f,-0.055f,0.008f,0.055f,0.72f,0.78f,0.84f);
    }
    private static void emitQuad(PoseStack.Pose pose,VertexConsumer buffer,
            float x1,float y1,float z1,float x2,float y2,float z2,float x3,float y3,float z3,
            float x4,float y4,float z4,float red,float green,float blue) {
        buffer.addVertex(pose,x1,y1,z1).setColor(red,green,blue,1.0f);
        buffer.addVertex(pose,x2,y2,z2).setColor(red,green,blue,1.0f);
        buffer.addVertex(pose,x3,y3,z3).setColor(red,green,blue,1.0f);
        buffer.addVertex(pose,x4,y4,z4).setColor(red,green,blue,1.0f);
    }
}