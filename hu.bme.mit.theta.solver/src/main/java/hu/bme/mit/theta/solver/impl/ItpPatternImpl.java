package hu.bme.mit.theta.solver.impl;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import hu.bme.mit.theta.solver.ItpMarker;
import hu.bme.mit.theta.solver.ItpPattern;

public class ItpPatternImpl implements ItpPattern {

	private ItpPattern parent;
	private final List<ItpPattern> children;

	private final ItpMarker marker;

	public ItpPatternImpl(final ItpMarker marker) {
		this.marker = checkNotNull(marker);
		children = new LinkedList<>();
	}

	@Override
	public ItpMarker getMarker() {
		return marker;
	}

	@Override
	public ItpPattern getParent() {
		return parent;
	}

	@Override
	public Collection<ItpPattern> getChildren() {
		return Collections.unmodifiableCollection(children);
	}

	@Override
	public ItpPattern createChild(final ItpMarker marker) {
		checkNotNull(marker);
		final ItpPatternImpl child = new ItpPatternImpl(marker);
		children.add(child);
		child.parent = this;
		return child;
	}

}