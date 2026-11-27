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
 * Renders a solid, four-arm sprinkler head as one rotating assembly.
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
        // Keep the head seated on the stationary central support.
        poseStack.translate(0.5, 0.625, 0.5);
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

        // Four substantial timber arms: each is a real cuboid, not a flat plane.
        for (int arm = 0; arm < 4; arm++) {
            float armRotation = rotation + arm * (float) (Math.PI / 2.0);

            // Main wooden arm, extending radially from the central hub.
            emitRotatedBox(pose, buffer, armRotation,
                    0.055f, 0.018f, -0.058f,
                    0.405f, 0.105f, 0.058f,
                    0.49f * shade, 0.30f * shade, 0.15f * shade);

            // Raised ridge along each arm adds a shaped, crafted top surface.
            emitRotatedBox(pose, buffer, armRotation,
                    0.105f, 0.105f, -0.035f,
                    0.335f, 0.125f, 0.035f,
                    0.61f * shade, 0.39f * shade, 0.21f * shade);

            // Chunky copper nozzle at the end of each rotating arm.
            emitRotatedBox(pose, buffer, armRotation,
                    0.355f, 0.004f, -0.078f,
                    0.445f, 0.082f, 0.078f,
                    0.55f * shade, 0.34f * shade, 0.18f * shade);

            // Dark outlet opening on the underside/end of the nozzle.
            emitRotatedBox(pose, buffer, armRotation,
                    0.414f, -0.002f, -0.043f,
                    0.447f, 0.018f, 0.043f,
                    0.25f * shade, 0.19f * shade, 0.13f * shade);
        }

        // A raised, solid central hub rotates together with all four arms.
        emitRotatedBox(pose, buffer, rotation,
                -0.105f, 0.012f, -0.105f,
                 0.105f, 0.125f,  0.105f,
                0.60f * shade, 0.38f * shade, 0.20f * shade);

        // Small top cap makes the centre look like a mechanical rotor joint.
        emitRotatedBox(pose, buffer, rotation,
                -0.065f, 0.125f, -0.065f,
                 0.065f, 0.153f,  0.065f,
                0.72f * shade, 0.49f * shade, 0.28f * shade);
    }

    /**
     * Emits all six faces of a cuboid, rotating its X/Z coordinates around
     * the sprinkler's vertical axis. This gives the rotor visible thickness
     * and side faces from every viewing angle.
     */
    private static void emitRotatedBox(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float angle,
            float minX, float minY, float minZ,
            float maxX, float maxY, float maxZ,
            float red, float green, float blue
    ) {
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);

        // Top and bottom.
        emitRotatedQuad(pose, buffer, angle,
                minX, maxY, minZ, maxX, maxY, minZ,
                maxX, maxY, maxZ, minX, maxY, maxZ,
                red * 1.12f, green * 1.12f, blue * 1.12f);
        emitRotatedQuad(pose, buffer, angle,
                minX, minY, maxZ, maxX, minY, maxZ,
                maxX, minY, minZ, minX, minY, minZ,
                red * 0.62f, green * 0.62f, blue * 0.62f);

        // Four vertical sides, shaded to make the arm read as a solid object.
        emitRotatedQuad(pose, buffer, angle,
                minX, minY, minZ, minX, maxY, minZ,
                maxX, maxY, minZ, maxX, minY, minZ,
                red * 0.82f, green * 0.82f, blue * 0.82f);
        emitRotatedQuad(pose, buffer, angle,
                maxX, minY, maxZ, maxX, maxY, maxZ,
                minX, maxY, maxZ, minX, minY, maxZ,
                red * 0.76f, green * 0.76f, blue * 0.76f);
        emitRotatedQuad(pose, buffer, angle,
                minX, minY, maxZ, minX, maxY, maxZ,
                minX, maxY, minZ, minX, minY, minZ,
                red * 0.90f, green * 0.90f, blue * 0.90f);
        emitRotatedQuad(pose, buffer, angle,
                maxX, minY, minZ, maxX, maxY, minZ,
                maxX, maxY, maxZ, maxX, minY, maxZ,
                red * 0.70f, green * 0.70f, blue * 0.70f);
    }

    private static void emitRotatedQuad(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float angle,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float x3, float y3, float z3,
            float x4, float y4, float z4,
            float red, float green, float blue
    ) {
        float c = (float) Math.cos(angle);
        float s = (float) Math.sin(angle);
        emitVertex(pose, buffer, x1, y1, z1, c, s, red, green, blue);
        emitVertex(pose, buffer, x2, y2, z2, c, s, red, green, blue);
        emitVertex(pose, buffer, x3, y3, z3, c, s, red, green, blue);
        emitVertex(pose, buffer, x4, y4, z4, c, s, red, green, blue);
    }

    private static void emitVertex(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            float x, float y, float z,
            float c, float s,
            float red, float green, float blue
    ) {
        float rotatedX = x * c - z * s;
        float rotatedZ = x * s + z * c;
        buffer.addVertex(pose, rotatedX, y, rotatedZ)
                .setColor(
                        Math.min(1.0f, red),
                        Math.min(1.0f, green),
                        Math.min(1.0f, blue),
                        1.0f
                );
    }
}
