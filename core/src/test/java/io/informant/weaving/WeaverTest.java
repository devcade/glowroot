/**
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.informant.weaving;

import static org.fest.assertions.api.Assertions.assertThat;
import io.informant.api.weaving.Mixin;
import io.informant.api.weaving.Pointcut;
import io.informant.weaving.SomeAspect.BasicAdvice;
import io.informant.weaving.SomeAspect.BasicMiscConstructorAdvice;
import io.informant.weaving.SomeAspect.BasicWithInnerClassArgAdvice;
import io.informant.weaving.SomeAspect.BrokenAdvice;
import io.informant.weaving.SomeAspect.ChangeReturnAdvice;
import io.informant.weaving.SomeAspect.CircularClassDependencyAdvice;
import io.informant.weaving.SomeAspect.HasString;
import io.informant.weaving.SomeAspect.HasStringClassMixin;
import io.informant.weaving.SomeAspect.HasStringInterfaceMixin;
import io.informant.weaving.SomeAspect.HasStringMultipleMixin;
import io.informant.weaving.SomeAspect.InjectAutoboxedReturnAdvice;
import io.informant.weaving.SomeAspect.InjectMethodArgAdvice;
import io.informant.weaving.SomeAspect.InjectMethodArgArrayAdvice;
import io.informant.weaving.SomeAspect.InjectMethodNameAdvice;
import io.informant.weaving.SomeAspect.InjectPrimitiveBooleanTravelerAdvice;
import io.informant.weaving.SomeAspect.InjectPrimitiveReturnAdvice;
import io.informant.weaving.SomeAspect.InjectPrimitiveTravelerAdvice;
import io.informant.weaving.SomeAspect.InjectReturnAdvice;
import io.informant.weaving.SomeAspect.InjectTargetAdvice;
import io.informant.weaving.SomeAspect.InjectThrowableAdvice;
import io.informant.weaving.SomeAspect.InjectTravelerAdvice;
import io.informant.weaving.SomeAspect.InnerMethodAdvice;
import io.informant.weaving.SomeAspect.InterfaceAppearsTwiceInHierarchyAdvice;
import io.informant.weaving.SomeAspect.MethodArgsDotDotAdvice1;
import io.informant.weaving.SomeAspect.MethodArgsDotDotAdvice2;
import io.informant.weaving.SomeAspect.MethodArgsDotDotAdvice3;
import io.informant.weaving.SomeAspect.MethodReturnStringAdvice;
import io.informant.weaving.SomeAspect.MethodReturnVoidAdvice;
import io.informant.weaving.SomeAspect.MoreVeryBadAdvice;
import io.informant.weaving.SomeAspect.MoreVeryBadAdvice2;
import io.informant.weaving.SomeAspect.MultipleMethodsAdvice;
import io.informant.weaving.SomeAspect.NonMatchingMethodReturnAdvice;
import io.informant.weaving.SomeAspect.NonMatchingMethodReturnAdvice2;
import io.informant.weaving.SomeAspect.NonMatchingStaticAdvice;
import io.informant.weaving.SomeAspect.NotNestingAdvice;
import io.informant.weaving.SomeAspect.NotNestingWithNoIsEnabledAdvice;
import io.informant.weaving.SomeAspect.PrimitiveAdvice;
import io.informant.weaving.SomeAspect.PrimitiveWithAutoboxAdvice;
import io.informant.weaving.SomeAspect.PrimitiveWithWildcardAdvice;
import io.informant.weaving.SomeAspect.StaticAdvice;
import io.informant.weaving.SomeAspect.StaticInjectTargetClassAdvice;
import io.informant.weaving.SomeAspect.TypeNamePatternAdvice;
import io.informant.weaving.SomeAspect.VeryBadAdvice;

import org.junit.Test;

import com.google.common.collect.ImmutableList;

/**
 * @author Trask Stalnaker
 * @since 0.5
 */
public class WeaverTest {

    // ===================== @IsEnabled =====================

    @Test
    public void shouldExecuteEnabledAdvice() throws Exception {
        // given
        BasicAdvice.resetThreadLocals();
        BasicAdvice.enable();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, BasicAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(BasicAdvice.onBeforeCount.get()).isEqualTo(1);
        assertThat(BasicAdvice.onReturnCount.get()).isEqualTo(1);
        assertThat(BasicAdvice.onThrowCount.get()).isEqualTo(0);
        assertThat(BasicAdvice.onAfterCount.get()).isEqualTo(1);
    }

    @Test
    public void shouldExecuteEnabledAdviceOnThrow() throws Exception {
        // given
        BasicAdvice.resetThreadLocals();
        BasicAdvice.enable();
        Misc test = newWovenObject(ThrowingMisc.class, Misc.class, BasicAdvice.class);
        // when
        try {
            test.execute1();
        } catch (Throwable t) {
        }
        // then
        assertThat(BasicAdvice.onBeforeCount.get()).isEqualTo(1);
        assertThat(BasicAdvice.onReturnCount.get()).isEqualTo(0);
        assertThat(BasicAdvice.onThrowCount.get()).isEqualTo(1);
        assertThat(BasicAdvice.onAfterCount.get()).isEqualTo(1);
    }

    @Test
    public void shouldNotExecuteDisabledAdvice() throws Exception {
        // given
        BasicAdvice.resetThreadLocals();
        BasicAdvice.disable();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, BasicAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(BasicAdvice.onBeforeCount.get()).isEqualTo(0);
        assertThat(BasicAdvice.onReturnCount.get()).isEqualTo(0);
        assertThat(BasicAdvice.onThrowCount.get()).isEqualTo(0);
        assertThat(BasicAdvice.onAfterCount.get()).isEqualTo(0);
    }

    @Test
    public void shouldNotExecuteDisabledAdviceOnThrow() throws Exception {
        // given
        BasicAdvice.resetThreadLocals();
        BasicAdvice.disable();
        Misc test = newWovenObject(ThrowingMisc.class, Misc.class, BasicAdvice.class);
        // when
        try {
            test.execute1();
        } catch (Throwable t) {
        }
        // then
        assertThat(BasicAdvice.onBeforeCount.get()).isEqualTo(0);
        assertThat(BasicAdvice.onReturnCount.get()).isEqualTo(0);
        assertThat(BasicAdvice.onThrowCount.get()).isEqualTo(0);
        assertThat(BasicAdvice.onAfterCount.get()).isEqualTo(0);
    }

    // ===================== @InjectTarget =====================

    @Test
    public void shouldInjectTarget() throws Exception {
        // given
        InjectTargetAdvice.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, InjectTargetAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(InjectTargetAdvice.isEnabledTarget.get()).isEqualTo(test);
        assertThat(InjectTargetAdvice.onBeforeTarget.get()).isEqualTo(test);
        assertThat(InjectTargetAdvice.onReturnTarget.get()).isEqualTo(test);
        assertThat(InjectTargetAdvice.onThrowTarget.get()).isNull();
        assertThat(InjectTargetAdvice.onAfterTarget.get()).isEqualTo(test);
    }

    @Test
    public void shouldInjectTargetOnThrow() throws Exception {
        // given
        InjectTargetAdvice.resetThreadLocals();
        Misc test = newWovenObject(ThrowingMisc.class, Misc.class, InjectTargetAdvice.class);
        // when
        try {
            test.execute1();
        } catch (Throwable t) {
        }
        // then
        assertThat(InjectTargetAdvice.isEnabledTarget.get()).isEqualTo(test);
        assertThat(InjectTargetAdvice.onBeforeTarget.get()).isEqualTo(test);
        assertThat(InjectTargetAdvice.onReturnTarget.get()).isNull();
        assertThat(InjectTargetAdvice.onThrowTarget.get()).isEqualTo(test);
        assertThat(InjectTargetAdvice.onAfterTarget.get()).isEqualTo(test);
    }

    // ===================== @InjectMethodArg =====================

    @Test
    public void shouldInjectMethodArgs() throws Exception {
        // given
        InjectMethodArgAdvice.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, InjectMethodArgAdvice.class);
        // when
        test.executeWithArgs("one", 2);
        // then
        Object[] parameters = new Object[] { "one", 2 };
        assertThat(InjectMethodArgAdvice.isEnabledParams.get()).isEqualTo(parameters);
        assertThat(InjectMethodArgAdvice.onBeforeParams.get()).isEqualTo(parameters);
        assertThat(InjectMethodArgAdvice.onReturnParams.get()).isEqualTo(parameters);
        assertThat(InjectMethodArgAdvice.onThrowParams.get()).isNull();
        assertThat(InjectMethodArgAdvice.onAfterParams.get()).isEqualTo(parameters);
    }

    @Test
    public void shouldInjectMethodArgOnThrow() throws Exception {
        // given
        InjectMethodArgAdvice.resetThreadLocals();
        Misc test = newWovenObject(ThrowingMisc.class, Misc.class, InjectMethodArgAdvice.class);
        // when
        try {
            test.executeWithArgs("one", 2);
        } catch (Throwable t) {
        }
        // then
        Object[] parameters = new Object[] { "one", 2 };
        assertThat(InjectMethodArgAdvice.isEnabledParams.get()).isEqualTo(parameters);
        assertThat(InjectMethodArgAdvice.onBeforeParams.get()).isEqualTo(parameters);
        assertThat(InjectMethodArgAdvice.onReturnParams.get()).isNull();
        assertThat(InjectMethodArgAdvice.onThrowParams.get()).isEqualTo(parameters);
        assertThat(InjectMethodArgAdvice.onAfterParams.get()).isEqualTo(parameters);
    }

    // ===================== @InjectMethodArgArray =====================

    @Test
    public void shouldInjectMethodArgArray() throws Exception {
        // given
        InjectMethodArgArrayAdvice.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, InjectMethodArgArrayAdvice.class);
        // when
        test.executeWithArgs("one", 2);
        // then
        Object[] parameters = new Object[] { "one", 2 };
        assertThat(InjectMethodArgArrayAdvice.isEnabledParams.get()).isEqualTo(parameters);
        assertThat(InjectMethodArgArrayAdvice.onBeforeParams.get()).isEqualTo(parameters);
        assertThat(InjectMethodArgArrayAdvice.onReturnParams.get()).isEqualTo(parameters);
        assertThat(InjectMethodArgArrayAdvice.onThrowParams.get()).isNull();
        assertThat(InjectMethodArgArrayAdvice.onAfterParams.get()).isEqualTo(parameters);
    }

    @Test
    public void shouldInjectMethodArgArrayOnThrow() throws Exception {
        // given
        InjectMethodArgArrayAdvice.resetThreadLocals();
        Misc test = newWovenObject(ThrowingMisc.class, Misc.class,
                InjectMethodArgArrayAdvice.class);
        // when
        try {
            test.executeWithArgs("one", 2);
        } catch (Throwable t) {
        }
        // then
        Object[] parameters = new Object[] { "one", 2 };
        assertThat(InjectMethodArgArrayAdvice.isEnabledParams.get()).isEqualTo(parameters);
        assertThat(InjectMethodArgArrayAdvice.onBeforeParams.get()).isEqualTo(parameters);
        assertThat(InjectMethodArgArrayAdvice.onReturnParams.get()).isNull();
        assertThat(InjectMethodArgArrayAdvice.onThrowParams.get()).isEqualTo(parameters);
        assertThat(InjectMethodArgArrayAdvice.onAfterParams.get()).isEqualTo(parameters);
    }

    // ===================== @InjectTraveler =====================

    @Test
    public void shouldInjectTraveler() throws Exception {
        // given
        InjectTravelerAdvice.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, InjectTravelerAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(InjectTravelerAdvice.onReturnTraveler.get()).isEqualTo("a traveler");
        assertThat(InjectTravelerAdvice.onThrowTraveler.get()).isNull();
        assertThat(InjectTravelerAdvice.onAfterTraveler.get()).isEqualTo("a traveler");
    }

    @Test
    public void shouldInjectPrimitiveTraveler() throws Exception {
        // given
        InjectPrimitiveTravelerAdvice.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class,
                InjectPrimitiveTravelerAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(InjectPrimitiveTravelerAdvice.onReturnTraveler.get()).isEqualTo(3);
        assertThat(InjectPrimitiveTravelerAdvice.onThrowTraveler.get()).isNull();
        assertThat(InjectPrimitiveTravelerAdvice.onAfterTraveler.get()).isEqualTo(3);
    }

    @Test
    public void shouldInjectPrimitiveBooleanTraveler() throws Exception {
        // given
        InjectPrimitiveBooleanTravelerAdvice.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class,
                InjectPrimitiveBooleanTravelerAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(InjectPrimitiveBooleanTravelerAdvice.onReturnTraveler.get()).isEqualTo(true);
        assertThat(InjectPrimitiveBooleanTravelerAdvice.onThrowTraveler.get()).isNull();
        assertThat(InjectPrimitiveBooleanTravelerAdvice.onAfterTraveler.get()).isEqualTo(true);
    }

    @Test
    public void shouldInjectTravelerOnThrow() throws Exception {
        // given
        InjectTravelerAdvice.resetThreadLocals();
        Misc test = newWovenObject(ThrowingMisc.class, Misc.class, InjectTravelerAdvice.class);
        // when
        try {
            test.execute1();
        } catch (Throwable t) {
        }
        // then
        assertThat(InjectTravelerAdvice.onReturnTraveler.get()).isNull();
        assertThat(InjectTravelerAdvice.onThrowTraveler.get()).isEqualTo("a traveler");
        assertThat(InjectTravelerAdvice.onAfterTraveler.get()).isEqualTo("a traveler");
    }

    // ===================== @InjectReturn =====================

    @Test
    public void shouldInjectReturn() throws Exception {
        // given
        InjectReturnAdvice.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, InjectReturnAdvice.class);
        // when
        test.executeWithReturn();
        // then
        assertThat(InjectReturnAdvice.returnValue.get()).isEqualTo("xyz");
    }

    @Test
    public void shouldInjectPrimitiveReturn() throws Exception {
        // given
        InjectPrimitiveReturnAdvice.resetThreadLocals();
        Misc test = newWovenObject(PrimitiveMisc.class, Misc.class,
                InjectPrimitiveReturnAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(InjectPrimitiveReturnAdvice.returnValue.get()).isEqualTo(4);
    }

    @Test
    public void shouldInjectAutoboxedReturn() throws Exception {
        // given
        InjectAutoboxedReturnAdvice.resetThreadLocals();
        Misc test = newWovenObject(PrimitiveMisc.class, Misc.class,
                InjectAutoboxedReturnAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(InjectAutoboxedReturnAdvice.returnValue.get()).isEqualTo(4);
    }

    // ===================== @InjectThrowable =====================

    @Test
    public void shouldInjectThrowable() throws Exception {
        // given
        InjectThrowableAdvice.resetThreadLocals();
        Misc test = newWovenObject(ThrowingMisc.class, Misc.class, InjectThrowableAdvice.class);
        // when
        try {
            test.execute1();
        } catch (Throwable t) {
        }
        // then
        assertThat(InjectThrowableAdvice.throwable.get()).isNotNull();
    }

    // ===================== @InjectMethodName =====================

    @Test
    public void shouldInjectMethodName() throws Exception {
        // given
        InjectMethodNameAdvice.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, InjectMethodNameAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(InjectMethodNameAdvice.isEnabledMethodName.get()).isEqualTo("execute1");
        assertThat(InjectMethodNameAdvice.onBeforeMethodName.get()).isEqualTo("execute1");
        assertThat(InjectMethodNameAdvice.onReturnMethodName.get()).isEqualTo("execute1");
        assertThat(InjectMethodNameAdvice.onThrowMethodName.get()).isNull();
        assertThat(InjectMethodNameAdvice.onAfterMethodName.get()).isEqualTo("execute1");
    }

    // ===================== change return value =====================

    @Test
    public void shouldChangeReturnValue() throws Exception {
        // given
        Misc test = newWovenObject(BasicMisc.class, Misc.class, ChangeReturnAdvice.class);
        // when
        String returnValue = test.executeWithReturn();
        // then
        assertThat(returnValue).isEqualTo("modified xyz");
    }

    // ===================== inheritance =====================

    @Test
    public void shouldNotWeaveIfDoesNotOverrideMatch() throws Exception {
        // given
        BasicAdvice.resetThreadLocals();
        Misc2 test = newWovenObject(BasicMisc.class, Misc2.class, BasicAdvice.class);
        // when
        test.execute2();
        // then
        assertThat(BasicAdvice.onBeforeCount.get()).isEqualTo(0);
        assertThat(BasicAdvice.onReturnCount.get()).isEqualTo(0);
        assertThat(BasicAdvice.onThrowCount.get()).isEqualTo(0);
        assertThat(BasicAdvice.onAfterCount.get()).isEqualTo(0);
    }

    // ===================== methodArgs '..' =====================

    @Test
    public void shouldMatchMethodArgsDotDot1() throws Exception {
        // given
        MethodArgsDotDotAdvice1.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, MethodArgsDotDotAdvice1.class);
        // when
        test.executeWithArgs("one", 2);
        // then
        assertThat(MethodArgsDotDotAdvice1.onBeforeCount.get()).isEqualTo(1);
    }

    @Test
    public void shouldMatchMethodArgsDotDot2() throws Exception {
        // given
        MethodArgsDotDotAdvice2.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, MethodArgsDotDotAdvice2.class);
        // when
        test.executeWithArgs("one", 2);
        // then
        assertThat(MethodArgsDotDotAdvice2.onBeforeCount.get()).isEqualTo(1);
    }

    @Test
    public void shouldMatchMethodArgsDotDot3() throws Exception {
        // given
        MethodArgsDotDotAdvice3.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, MethodArgsDotDotAdvice3.class);
        // when
        test.executeWithArgs("one", 2);
        // then
        assertThat(MethodArgsDotDotAdvice3.onBeforeCount.get()).isEqualTo(1);
    }

    // ===================== @Mixin =====================

    @Test
    public void shouldMixinToClass() throws Exception {
        // given
        Misc test = newWovenObject(BasicMisc.class, Misc.class, HasStringClassMixin.class,
                HasString.class);
        // when
        ((HasString) test).setString("another value");
        // then
        assertThat(((HasString) test).getString()).isEqualTo("another value");
    }

    @Test
    public void shouldMixinToInterface() throws Exception {
        // given
        Misc test = newWovenObject(BasicMisc.class, Misc.class, HasStringInterfaceMixin.class,
                HasString.class);
        // when
        ((HasString) test).setString("another value");
        // then
        assertThat(((HasString) test).getString()).isEqualTo("another value");
    }

    @Test
    public void shouldMixinOnlyOnce() throws Exception {
        // given
        Misc test = newWovenObject(BasicMisc.class, Misc.class, HasStringMultipleMixin.class,
                HasString.class);
        // when
        ((HasString) test).setString("another value");
        // then
        assertThat(((HasString) test).getString()).isEqualTo("another value");
    }

    @Test
    public void shouldMixinAndCallInitExactlyOnce() throws Exception {
        // given
        Misc test = newWovenObject(BasicMisc.class, Misc.class, HasStringClassMixin.class,
                HasString.class);
        // when
        // then
        assertThat(((HasString) test).getString()).isEqualTo("a string");
    }

    // ===================== @Pointcut.nestable =====================

    @Test
    public void shouldNotNestPointcuts() throws Exception {
        // given
        NotNestingAdvice.resetThreadLocals();
        Misc test = newWovenObject(NestingMisc.class, Misc.class, NotNestingAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(NotNestingAdvice.onBeforeCount.get()).isEqualTo(1);
        assertThat(NotNestingAdvice.onReturnCount.get()).isEqualTo(1);
        assertThat(NotNestingAdvice.onThrowCount.get()).isEqualTo(0);
        assertThat(NotNestingAdvice.onAfterCount.get()).isEqualTo(1);
        assertThat(test.executeWithReturn()).isEqualTo("yes");
    }

    @Test
    public void shouldNotNestPointcuts2() throws Exception {
        // given
        NotNestingAdvice.resetThreadLocals();
        Misc test = newWovenObject(NestingMisc.class, Misc.class, NotNestingAdvice.class);
        // when
        test.execute1();
        test.execute1();
        // then
        assertThat(NotNestingAdvice.onBeforeCount.get()).isEqualTo(2);
        assertThat(NotNestingAdvice.onReturnCount.get()).isEqualTo(2);
        assertThat(NotNestingAdvice.onThrowCount.get()).isEqualTo(0);
        assertThat(NotNestingAdvice.onAfterCount.get()).isEqualTo(2);
        assertThat(test.executeWithReturn()).isEqualTo("yes");
    }

    @Test
    public void shouldNotNestPointcuts3() throws Exception {
        // given
        NotNestingAdvice.resetThreadLocals();
        Misc test = newWovenObject(NestingAnotherMisc.class, Misc.class, NotNestingAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(NotNestingAdvice.onBeforeCount.get()).isEqualTo(1);
        assertThat(NotNestingAdvice.onReturnCount.get()).isEqualTo(1);
        assertThat(NotNestingAdvice.onThrowCount.get()).isEqualTo(0);
        assertThat(NotNestingAdvice.onAfterCount.get()).isEqualTo(1);
        assertThat(test.executeWithReturn()).isEqualTo("yes");
    }

    @Test
    public void shouldNestPointcuts() throws Exception {
        // given
        BasicAdvice.resetThreadLocals();
        Misc test = newWovenObject(NestingMisc.class, Misc.class, BasicAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(BasicAdvice.onBeforeCount.get()).isEqualTo(2);
        assertThat(BasicAdvice.onReturnCount.get()).isEqualTo(2);
        assertThat(BasicAdvice.onThrowCount.get()).isEqualTo(0);
        assertThat(BasicAdvice.onAfterCount.get()).isEqualTo(2);
    }

    @Test
    public void shouldNotNestPointcutsEvenWithNoIsEnabled() throws Exception {
        // given
        NotNestingAdvice.resetThreadLocals();
        Misc test = newWovenObject(NestingMisc.class, Misc.class,
                NotNestingWithNoIsEnabledAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(NotNestingWithNoIsEnabledAdvice.onBeforeCount.get()).isEqualTo(1);
        assertThat(NotNestingWithNoIsEnabledAdvice.onReturnCount.get()).isEqualTo(1);
        assertThat(NotNestingWithNoIsEnabledAdvice.onThrowCount.get()).isEqualTo(0);
        assertThat(NotNestingWithNoIsEnabledAdvice.onAfterCount.get()).isEqualTo(1);
        assertThat(test.executeWithReturn()).isEqualTo("yes");
    }

    // ===================== @Pointcut.innerMethod =====================

    @Test
    public void shouldWrapInMarkerMethod() throws Exception {
        // given
        Misc test = newWovenObject(InnerMethodMisc.class, Misc.class, InnerMethodAdvice.class);
        // when
        String methodName = test.executeWithReturn();
        // then
        assertThat(methodName).isNotNull();
        assertThat(methodName).matches("executeWithReturn\\$informant\\$metric\\$abc\\$xyz\\$\\d+");
    }

    // ===================== static pointcuts =====================

    @Test
    public void shouldWeaveStaticMethod() throws Exception {
        // given
        StaticAdvice.resetThreadLocals();
        StaticAdvice.enable();
        Misc test = newWovenObject(StaticMisc.class, Misc.class, StaticAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(StaticAdvice.onBeforeCount.get()).isEqualTo(1);
        assertThat(StaticAdvice.onReturnCount.get()).isEqualTo(1);
        assertThat(StaticAdvice.onThrowCount.get()).isEqualTo(0);
        assertThat(StaticAdvice.onAfterCount.get()).isEqualTo(1);
    }

    @Test
    public void shouldWeaveStaticMethodInjectTargetClass() throws Exception {
        // given
        StaticInjectTargetClassAdvice.resetThreadLocals();
        Misc test = newWovenObject(StaticMisc.class, Misc.class,
                StaticInjectTargetClassAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(StaticInjectTargetClassAdvice.onBeforeCount.get().getName()).isEqualTo(
                StaticMisc.class.getName());
    }
    // ===================== primitive args =====================

    @Test
    public void shouldWeaveMethodWithPrimitiveArgs() throws Exception {
        // given
        PrimitiveAdvice.resetThreadLocals();
        PrimitiveAdvice.enable();
        Misc test = newWovenObject(PrimitiveMisc.class, Misc.class, PrimitiveAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(PrimitiveAdvice.onBeforeCount.get()).isEqualTo(1);
        assertThat(PrimitiveAdvice.onReturnCount.get()).isEqualTo(1);
        assertThat(PrimitiveAdvice.onThrowCount.get()).isEqualTo(0);
        assertThat(PrimitiveAdvice.onAfterCount.get()).isEqualTo(1);
    }

    // ===================== wildcard args =====================

    @Test
    public void shouldWeaveMethodWithWildcardArgs() throws Exception {
        // given
        PrimitiveWithWildcardAdvice.resetThreadLocals();
        Misc test = newWovenObject(PrimitiveMisc.class, Misc.class,
                PrimitiveWithWildcardAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(PrimitiveWithWildcardAdvice.enabledCount.get()).isEqualTo(1);
        assertThat(PrimitiveWithWildcardAdvice.onBeforeCount.get()).isEqualTo(1);
    }

    // ===================== type name pattern =====================

    @Test
    public void shouldWeaveTypeWithNamePattern() throws Exception {
        // given
        TypeNamePatternAdvice.resetThreadLocals();
        Misc test = newWovenObject(PrimitiveMisc.class, Misc.class, TypeNamePatternAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(TypeNamePatternAdvice.enabledCount.get()).isEqualTo(1);
        assertThat(TypeNamePatternAdvice.onBeforeCount.get()).isEqualTo(1);
    }

    // ===================== autobox args =====================

    @Test
    public void shouldWeaveMethodWithAutoboxArgs() throws Exception {
        // given
        PrimitiveWithAutoboxAdvice.resetThreadLocals();
        Misc test = newWovenObject(PrimitiveMisc.class, Misc.class,
                PrimitiveWithAutoboxAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(PrimitiveWithAutoboxAdvice.enabledCount.get()).isEqualTo(1);
    }

    // ===================== return type matching =====================

    @Test
    public void shouldMatchMethodReturningVoid() throws Exception {
        // given
        MethodReturnVoidAdvice.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, MethodReturnVoidAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(MethodReturnVoidAdvice.onBeforeCount.get()).isEqualTo(1);
        assertThat(MethodReturnVoidAdvice.onReturnCount.get()).isEqualTo(1);
    }

    @Test
    public void shouldMatchMethodReturningString() throws Exception {
        // given
        MethodReturnStringAdvice.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, MethodReturnStringAdvice.class);
        // when
        test.executeWithReturn();
        // then
        assertThat(MethodReturnStringAdvice.onBeforeCount.get()).isEqualTo(1);
        assertThat(MethodReturnStringAdvice.onReturnCount.get()).isEqualTo(1);
    }

    @Test
    public void shouldNotMatchMethodBasedOnReturnType() throws Exception {
        // given
        NonMatchingMethodReturnAdvice.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class,
                NonMatchingMethodReturnAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(NonMatchingMethodReturnAdvice.onBeforeCount.get()).isEqualTo(0);
        assertThat(NonMatchingMethodReturnAdvice.onReturnCount.get()).isEqualTo(0);
    }

    @Test
    public void shouldNotMatchMethodBasedOnReturnType2() throws Exception {
        // given
        NonMatchingMethodReturnAdvice2.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class,
                NonMatchingMethodReturnAdvice2.class);
        // when
        test.executeWithReturn();
        // then
        assertThat(NonMatchingMethodReturnAdvice2.onBeforeCount.get()).isEqualTo(0);
        assertThat(NonMatchingMethodReturnAdvice2.onReturnCount.get()).isEqualTo(0);
    }

    // ===================== constructor =====================

    @Test
    // TODO handle @OnBefore constructor
    // TODO what about constructing objects that implement a given interface?
    public void shouldHandleConstructorPointcut() throws Exception {
        // given
        Misc test = newWovenObject(BasicMisc.class, Misc.class, BasicMiscConstructorAdvice.class);
        // reset thread locals after instantiated BasicMisc, to avoid counting that constructor call
        BasicMiscConstructorAdvice.resetThreadLocals();
        // when
        test.execute1();
        // then
        assertThat(BasicMiscConstructorAdvice.enabledCount.get()).isEqualTo(1);
        // assertThat(BasicConstructorAdvice.onBeforeCount.get()).isEqualTo(1);
        assertThat(BasicMiscConstructorAdvice.onReturnCount.get()).isEqualTo(1);
        assertThat(BasicMiscConstructorAdvice.onThrowCount.get()).isEqualTo(0);
        assertThat(BasicMiscConstructorAdvice.onAfterCount.get()).isEqualTo(1);
    }

    @Test
    public void shouldHandleInnerClassArg() throws Exception {
        // given
        BasicWithInnerClassArgAdvice.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, BasicWithInnerClassArgAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(BasicWithInnerClassArgAdvice.enabledCount.get()).isEqualTo(1);
        assertThat(BasicWithInnerClassArgAdvice.onBeforeCount.get()).isEqualTo(1);
        assertThat(BasicWithInnerClassArgAdvice.onReturnCount.get()).isEqualTo(1);
        assertThat(BasicWithInnerClassArgAdvice.onThrowCount.get()).isEqualTo(0);
        assertThat(BasicWithInnerClassArgAdvice.onAfterCount.get()).isEqualTo(1);
    }

    @Test
    public void shouldHandlePointcutWithMultipleMethods() throws Exception {
        // given
        MultipleMethodsAdvice.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, MultipleMethodsAdvice.class);
        // when
        test.execute1();
        test.executeWithArgs("one", 2);
        // then
        assertThat(MultipleMethodsAdvice.onBeforeCount.get()).isEqualTo(2);
        assertThat(MultipleMethodsAdvice.onReturnCount.get()).isEqualTo(2);
        assertThat(MultipleMethodsAdvice.onThrowCount.get()).isEqualTo(0);
        assertThat(MultipleMethodsAdvice.onAfterCount.get()).isEqualTo(2);
    }

    @Test
    public void shouldNotDisruptInnerTryCatch() throws Exception {
        // given
        Misc test = newWovenObject(InnerTryCatchMisc.class, Misc.class, BasicAdvice.class,
                HasString.class);
        // when
        test.execute1();
        // then
        assertThat(test.executeWithReturn()).isEqualTo("caught");
    }

    @Test
    public void shouldPayAttentionToStaticKeyword() throws Exception {
        // given
        NonMatchingStaticAdvice.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, NonMatchingStaticAdvice.class);
        // when
        test.execute1();
        // then
        assertThat(NonMatchingStaticAdvice.onBeforeCount.get()).isEqualTo(0);
        assertThat(NonMatchingStaticAdvice.onReturnCount.get()).isEqualTo(0);
        assertThat(NonMatchingStaticAdvice.onThrowCount.get()).isEqualTo(0);
        assertThat(NonMatchingStaticAdvice.onAfterCount.get()).isEqualTo(0);
    }

    @Test
    public void shouldNotBomb() throws Exception {
        // given
        Misc test = newWovenObject(BasicMisc.class, Misc.class, BrokenAdvice.class);
        // when
        test.executeWithArgs("one", 2);
        // then should not bomb
    }

    @Test
    public void shouldNotCallOnThrowForOnBeforeException() throws Exception {
        // given
        VeryBadAdvice.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, VeryBadAdvice.class);
        // when
        try {
            test.executeWithArgs("one", 2);
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("Sorry");
            assertThat(VeryBadAdvice.onBeforeCount.get()).isEqualTo(1);
            assertThat(VeryBadAdvice.onThrowCount.get()).isEqualTo(0);
            assertThat(VeryBadAdvice.onAfterCount.get()).isEqualTo(0);
            return;
        }
        throw new AssertionError("Expecting IllegalStateException");
    }

    @Test
    public void shouldNotCallOnThrowForOnReturnException() throws Exception {
        // given
        MoreVeryBadAdvice.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, MoreVeryBadAdvice.class);
        // when
        try {
            test.executeWithArgs("one", 2);
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("Sorry");
            assertThat(MoreVeryBadAdvice.onReturnCount.get()).isEqualTo(1);
            assertThat(MoreVeryBadAdvice.onThrowCount.get()).isEqualTo(0);
            assertThat(MoreVeryBadAdvice.onAfterCount.get()).isEqualTo(0);
            return;
        }
        throw new AssertionError("Expecting IllegalStateException");
    }

    // same as MoreVeryBadAdvice, but testing weaving a method with a non-void return type
    @Test
    public void shouldNotCallOnThrowForOnReturnException2() throws Exception {
        // given
        MoreVeryBadAdvice2.resetThreadLocals();
        Misc test = newWovenObject(BasicMisc.class, Misc.class, MoreVeryBadAdvice2.class);
        // when
        try {
            test.executeWithReturn();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("Sorry");
            assertThat(MoreVeryBadAdvice2.onReturnCount.get()).isEqualTo(1);
            assertThat(MoreVeryBadAdvice2.onThrowCount.get()).isEqualTo(0);
            assertThat(MoreVeryBadAdvice2.onAfterCount.get()).isEqualTo(0);
            return;
        }
        throw new AssertionError("Expecting IllegalStateException");
    }

    @Test
    public void shouldNotBomb2() throws Exception {
        // given
        Misc test = newWovenObject(AccessibilityMisc.class, Misc.class, BasicAdvice.class);
        // when
        test.execute1();
        // then should not bomb
    }

    @Test
    // weaving an interface method that references a concrete class that implements that interface
    // is supported
    public void shouldHandleCircularDependency() throws Exception {
        // given
        CircularClassDependencyAdvice.resetThreadLocals();
        // when
        newWovenObject(BasicMisc.class, Misc.class, CircularClassDependencyAdvice.class);
        // then should not bomb
    }

    @Test
    // weaving an interface method that appears twice in a given class hierarchy should only weave
    // the method once
    public void shouldHandleInterfaceThatAppearsTwiceInHierarchy() throws Exception {
        // given
        InterfaceAppearsTwiceInHierarchyAdvice.resetThreadLocals();
        // when
        Misc test = newWovenObject(SubBasicMisc.class, Misc.class,
                InterfaceAppearsTwiceInHierarchyAdvice.class);
        test.execute1();
        // then
        assertThat(InterfaceAppearsTwiceInHierarchyAdvice.onBeforeCount.get()).isEqualTo(1);
    }

    public static <S, T extends S> S newWovenObject(Class<T> implClass, Class<S> bridgeClass,
            Class<?> adviceClass, Class<?>... extraBridgeClasses) throws Exception {

        IsolatedWeavingClassLoader.Builder loader = IsolatedWeavingClassLoader.builder();
        Pointcut pointcut = adviceClass.getAnnotation(Pointcut.class);
        if (pointcut != null) {
            loader.setAdvisors(ImmutableList.of(Advice.from(pointcut, adviceClass)));
        }
        Mixin mixin = adviceClass.getAnnotation(Mixin.class);
        if (mixin != null) {
            loader.setMixinTypes(ImmutableList.of(MixinType.from(mixin, adviceClass)));
        }
        // adviceClass is passed as bridgeable so that the static threadlocals will be accessible
        // for test verification
        loader.addBridgeClasses(bridgeClass, adviceClass);
        loader.addBridgeClasses(extraBridgeClasses);
        return loader.build().newInstance(implClass, bridgeClass);
    }
}