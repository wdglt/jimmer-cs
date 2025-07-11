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

import org.babyfish.jimmer.impl.asm.ClassVisitor;

/**
 * A node that represents an inner class. This inner class is not necessarily a member of the {@link
 * ClassNode} containing this object. More precisely, every class or interface C which is referenced
 * by a {@link ClassNode} and which is not a package member must be represented with an {@link
 * InnerClassNode}. The {@link ClassNode} must reference its nested class or interface members, and
 * its enclosing class, if any. See the JVMS 4.7.6 section for more details.
 *
 * @author Eric Bruneton
 */
public class InnerClassNode {

  /** The internal name of an inner class (see {@link org.babyfish.jimmer.impl.asm.Type#getInternalName()}). */
  public String name;

  /**
   * The internal name of the class to which the inner class belongs (see {@link
   * org.babyfish.jimmer.impl.asm.Type#getInternalName()}). May be {@literal null}.
   */
  public String outerName;

  /**
   * The (simple) name of the inner class inside its enclosing class. Must be {@literal null} if the
   * inner class is not the member of a class or interface (e.g. for local or anonymous classes).
   */
  public String innerName;

  /**
   * The access flags of the inner class as originally declared in the source code from which the
   * class was compiled.
   */
  public int access;

  /**
   * Constructs a new {@link InnerClassNode} for an inner class C.
   *
   * @param name the internal name of C (see {@link org.babyfish.jimmer.impl.asm.Type#getInternalName()}).
   * @param outerName the internal name of the class or interface C is a member of (see {@link
   *     org.babyfish.jimmer.impl.asm.Type#getInternalName()}). Must be {@literal null} if C is not the member
   *     of a class or interface (e.g. for local or anonymous classes).
   * @param innerName the (simple) name of C. Must be {@literal null} for anonymous inner classes.
   * @param access the access flags of C originally declared in the source code from which this
   *     class was compiled.
   */
  public InnerClassNode(
      final String name, final String outerName, final String innerName, final int access) {
    this.name = name;
    this.outerName = outerName;
    this.innerName = innerName;
    this.access = access;
  }

  /**
   * Makes the given class visitor visit this inner class.
   *
   * @param classVisitor a class visitor.
   */
  public void accept(final ClassVisitor classVisitor) {
    classVisitor.visitInnerClass(name, outerName, innerName, access);
  }
}
