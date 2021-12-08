package com.jozufozu.flywheel.backend.material.batching;

import java.util.HashMap;
import java.util.Map;

import com.jozufozu.flywheel.backend.instancing.InstanceData;
import com.jozufozu.flywheel.backend.material.MaterialGroup;
import com.jozufozu.flywheel.backend.material.MaterialSpec;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;

public class BatchedMaterialGroup implements MaterialGroup {

	protected final RenderType state;

	private final Map<MaterialSpec<?>, BatchedMaterial<?>> materials = new HashMap<>();

	public BatchedMaterialGroup(RenderType state) {
		this.state = state;
	}

	/**
	 * Get the material as defined by the given {@link MaterialSpec spec}.
	 * @param spec The material you want to create instances with.
	 * @param <D> The type representing the per instance data.
	 * @return A
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <D extends InstanceData> BatchedMaterial<D> material(MaterialSpec<D> spec) {
		return (BatchedMaterial<D>) materials.computeIfAbsent(spec, BatchedMaterial::new);
	}

	public void render(PoseStack stack, MultiBufferSource source) {
		VertexConsumer buffer = source.getBuffer(state);

		for (BatchedMaterial<?> value : materials.values()) {
			value.render(stack, buffer);
		}
	}

	public void clear() {
		materials.values().forEach(BatchedMaterial::clear);
	}

	public void delete() {
		materials.clear();
	}
}