// ASM: a very small and fast Java bytecode manipulation framework
// Copyright (c) 2000-2011 INRIA, France Telecom
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions
// are met:
// 1. Redistributions of source code must retain the above copyright
//    notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
//    notice, this list of conditions and the following disclaimer in the
//    documentation and/or other materials provided with the distribution.
// 3. Neither the name of the copyright holders nor the names of its
//    contributors may be used to endorse or promote products derived from
//    this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
// THE POSSIBILITY OF SUCH DAMAGE.
package org.babyfish.jimmer.impl.asm.tree;

import java.util.List;
import java.util.Map;
import org.babyfish.jimmer.impl.asm.Label;
import org.babyfish.jimmer.impl.asm.MethodVisitor;
import org.babyfish.jimmer.impl.asm.Opcodes;
import org.babyfish.jimmer.impl.asm.tree.Util;

/**
 * A node that represents a TABLESWITCH instruction.
 *
 * @author Eric Bruneton
 */
public class TableSwitchInsnNode extends AbstractInsnNode {

  /** The minimum key value. */
  public int min;

  /** The maximum key value. */
  public int max;

  /** Beginning of the default handler block. */
  public LabelNode dflt;

  /** Beginnings of the handler blocks. This list is a list of {@link LabelNode} objects. */
  public List<LabelNode> labels;

  /**
   * Constructs a new {@link TableSwitchInsnNode}.
   *
   * @param min the minimum key value.
   * @param max the maximum key value.
   * @param dflt beginning of the default handler block.
   * @param labels beginnings of the handler blocks. {@code labels[i]} is the beginning of the
   *     handler block for the {@code min + i} key.
   */
  public TableSwitchInsnNode(
      final int min, final int max, final LabelNode dflt, final LabelNode... labels) {
    super(Opcodes.TABLESWITCH);
    this.min = min;
    this.max = max;
    this.dflt = dflt;
    this.labels = Util.asArrayList(labels);
  }

  @Override
  public int getType() {
    return TABLESWITCH_INSN;
  }

  @Override
  public void accept(final MethodVisitor methodVisitor) {
    Label[] labelsArray = new Label[this.labels.size()];
    for (int i = 0, n = labelsArray.length; i < n; ++i) {
      labelsArray[i] = this.labels.get(i).getLabel();
    }
    methodVisitor.visitTableSwitchInsn(min, max, dflt.getLabel(), labelsArray);
    acceptAnnotations(methodVisitor);
  }

  @Override
  public AbstractInsnNode clone(final Map<LabelNode, LabelNode> clonedLabels) {
    return new TableSwitchInsnNode(min, max, clone(dflt, clonedLabels), clone(labels, clonedLabels))
        .cloneAnnotations(this);
  }
}
