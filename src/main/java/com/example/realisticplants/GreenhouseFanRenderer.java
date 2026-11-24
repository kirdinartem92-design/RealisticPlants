package com.realisticplants;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.camera.CameraRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.world.phys.Vec3;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

/**
 * Procedurally renders the greenhouse fan's three-blade rotor.
 * The rotor is drawn on the front face of the fan block and smoothly
 * accelerates/decelerates using the client-side animation state.
 */
public final class GreenhouseFanRenderer
        implements BlockEntityRenderer<GreenhouseFanBlockEntity, GreenhouseFanRenderer.State> {

    public GreenhouseFanRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends BlockEntityRenderState {
        public float angle;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(
            GreenhouseFanBlockEntity fan,
            State state,
            float partialTick,
            Vec3 cameraPosition,
            net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(
                fan, state, partialTick, cameraPosition, breakProgress
        );
        state.angle = fan.getInterpolatedRotation(partialTick);
    }

    @Override
    public void submit(
            State state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            CameraRenderState camera
    ) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 1.006);

        float angle = state.angle;
        collector.submitCustomGeometry(
                poseStack,
                RenderTypes.lightning(),
                (pose, buffer) -> renderRotor(pose, buffer, angle)
        );

        poseStack.popPose();
    }

    private static void renderRotor(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float angleDegrees
    ) {
        float angleOffset = (float) Math.toRadians(angleDegrees);

        // Three broad, slightly swept blades.
        for (int blade = 0; blade < 3; blade++) {
            float angle = angleOffset + blade * (float) (Math.PI * 2.0 / 3.0);
            emitBlade(pose, buffer, angle);
        }

        // A compact square hub covers the rotor axle.
        emitQuad(
                pose, buffer,
                -0.055f, -0.055f, 0.0f,
                 0.055f, -0.055f, 0.0f,
                 0.055f,  0.055f, 0.0f,
                -0.055f,  0.055f, 0.0f,
                0.62f, 0.66f, 0.70f
        );
    }

    private static void emitBlade(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float angle
    ) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);

        // Inner and outer radii, with a swept outer edge.
        float inner = 0.045f;
        float outer = 0.39f;
        float innerWidth = 0.055f;
        float outerWidth = 0.105f;
        float sweep = 0.075f;

        float x1 = cos * inner - sin * innerWidth;
        float y1 = sin * inner + cos * innerWidth;
        float x2 = cos * inner + sin * innerWidth;
        float y2 = sin * inner - cos * innerWidth;
        float x3 = cos * outer - sin * (outerWidth + sweep);
        float y3 = sin * outer + cos * (outerWidth + sweep);
        float x4 = cos * outer + sin * (outerWidth - sweep);
        float y4 = sin * outer - cos * (outerWidth - sweep);

        emitQuad(
                pose, buffer,
                x1, y1, 0.0f,
                x2, y2, 0.0f,
                x4, y4, 0.0f,
                x3, y3, 0.0f,
                0.48f, 0.52f, 0.57f
        );
    }

    private static void emitQuad(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            float red, float green, float blue
    ) {
        buffer.addVertex(pose, x1, y1, z1).setColor(red, green, blue, 1.0f);
        buffer.addVertex(pose, x2, y2, z2).setColor(red, green, blue, 1.0f);
        buffer.addVertex(pose, x3, y3, z3).setColor(red, green, blue, 1.0f);
        buffer.addVertex(pose, x4, y4, z4).setColor(red, green, blue, 1.0f);
    }
}
