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

/**
 * Renders the sprinkler's wooden four-outlet head as one rotating assembly.
 * The support column and lower collar remain part of the static block model.
 */
public final class IrrigationSprinklerRenderer
        implements BlockEntityRenderer<IrrigationSprinklerBlockEntity, IrrigationSprinklerRenderer.State> {

    public IrrigationSprinklerRenderer(BlockEntityRendererProvider.Context context) {
    }

    public static final class State extends BlockEntityRenderState {
        public float angle;
        public boolean active;
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(
            IrrigationSprinklerBlockEntity sprinkler,
            State state,
            float partialTick,
            Vec3 cameraPosition,
            net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay breakProgress
    ) {
        BlockEntityRenderer.super.extractRenderState(
                sprinkler, state, partialTick, cameraPosition, breakProgress
        );
        state.angle = sprinkler.getInterpolatedRotation(partialTick);
        state.active = sprinkler.getBlockState().getValue(IrrigationSprinklerBlock.ACTIVE);
    }

    @Override
    public void submit(
            State state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            CameraRenderState camera
    ) {
        poseStack.pushPose();
        // The wooden head sits directly on the fixed central stem/collar.
        poseStack.translate(0.5, 0.632, 0.5);
        float angle = state.angle;
        boolean active = state.active;
        collector.submitCustomGeometry(
                poseStack,
                RenderTypes.debugQuads(),
                (pose, buffer) -> renderRotatingHead(pose, buffer, angle, active)
        );
        poseStack.popPose();
    }

    private static void renderRotatingHead(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float angleDegrees,
            boolean active
    ) {
        float rotation = (float) Math.toRadians(angleDegrees);
        float shade = active ? 1.0f : 0.88f;

        // Four wooden outlet arms rotate together around the stationary stem.
        for (int arm = 0; arm < 4; arm++) {
            float a = rotation + arm * (float) (Math.PI / 2.0);
            float c = (float) Math.cos(a);
            float s = (float) Math.sin(a);
            float inner = 0.025f;
            float outer = 0.405f;
            float innerWidth = 0.050f;
            float outerWidth = 0.060f;

            float x1 = c * inner - s * innerWidth;
            float z1 = s * inner + c * innerWidth;
            float x2 = c * inner + s * innerWidth;
            float z2 = s * inner - c * innerWidth;
            float x3 = c * outer + s * outerWidth;
            float z3 = s * outer - c * outerWidth;
            float x4 = c * outer - s * outerWidth;
            float z4 = s * outer + c * outerWidth;

            // Warm timber colour, with a slightly darker outer nozzle end.
            emitQuad(pose, buffer,
                    x1, 0.0f, z1,
                    x2, 0.0f, z2,
                    x3, 0.0f, z3,
                    x4, 0.0f, z4,
                    0.48f * shade, 0.29f * shade, 0.14f * shade);
        }

        // Wooden central hub is part of the rotating head, not the fixed stem.
        emitQuad(pose, buffer,
                -0.070f, 0.008f, -0.070f,
                 0.070f, 0.008f, -0.070f,
                 0.070f, 0.008f,  0.070f,
                -0.070f, 0.008f,  0.070f,
                0.57f * shade, 0.36f * shade, 0.19f * shade);
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
