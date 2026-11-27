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
 * Clearly visible four-arm rotating sprinkler head, rendered above the fixed
 * copper support. The rotor is deliberately raised and brightly coloured so
 * its movement is distinguishable from the static cross-shaped model below.
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
        // Keep the rotor clearly above the fixed nozzle arms in the block model.
        poseStack.translate(0.5, 0.755, 0.5);
        float angle = state.angle;
        boolean active = state.active;
        collector.submitCustomGeometry(
                poseStack,
                RenderTypes.debugQuads(),
                (pose, buffer) -> renderRotor(pose, buffer, angle, active)
        );
        poseStack.popPose();
    }

    private static void renderRotor(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float angleDegrees,
            boolean active
    ) {
        float rotation = (float) Math.toRadians(angleDegrees);
        float brightness = active ? 1.0f : 0.78f;

        // Four broad, swept blades. The contrasting cyan-metal finish makes
        // the rotation easy to see against the copper-coloured fixed base.
        for (int blade = 0; blade < 4; blade++) {
            float a = rotation + blade * (float) (Math.PI / 2.0);
            float c = (float) Math.cos(a);
            float s = (float) Math.sin(a);
            float inner = 0.045f;
            float outer = 0.43f;
            float innerWidth = 0.035f;
            float outerWidth = 0.075f;

            float x1 = c * inner - s * innerWidth;
            float z1 = s * inner + c * innerWidth;
            float x2 = c * inner + s * innerWidth;
            float z2 = s * inner - c * innerWidth;
            float x3 = c * outer + s * outerWidth;
            float z3 = s * outer - c * outerWidth;
            float x4 = c * outer - s * outerWidth;
            float z4 = s * outer + c * outerWidth;

            emitQuad(pose, buffer,
                    x1, 0.0f, z1,
                    x2, 0.0f, z2,
                    x3, 0.0f, z3,
                    x4, 0.0f, z4,
                    0.25f * brightness, 0.82f * brightness, 0.96f * brightness);
        }

        // Raised central hub.
        emitQuad(pose, buffer,
                -0.065f, 0.012f, -0.065f,
                 0.065f, 0.012f, -0.065f,
                 0.065f, 0.012f,  0.065f,
                -0.065f, 0.012f,  0.065f,
                0.88f, 0.94f, 1.0f);
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
