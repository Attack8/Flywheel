package com.jozufozu.flywheel.core.structs;

import com.jozufozu.flywheel.api.struct.StructType;
import com.jozufozu.flywheel.core.structs.model.TransformedPart;
import com.jozufozu.flywheel.core.structs.model.TransformedType;
import com.jozufozu.flywheel.core.structs.oriented.OrientedPart;
import com.jozufozu.flywheel.core.structs.oriented.OrientedType;

public class StructTypes {
	public static final StructType<TransformedPart> TRANSFORMED = new TransformedType();
	public static final StructType<OrientedPart> ORIENTED = new OrientedType();
}