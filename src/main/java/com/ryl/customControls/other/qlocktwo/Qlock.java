/*
 * Copyright (c) 2013 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ryl.customControls.other.qlocktwo;

import java.util.List;


/**
 * Created by
 * User: hansolo
 * Date: 27.02.13
 * Time: 15:43
 */
public interface Qlock {
    public String[][] getMatrix();

    public List<QlockWord> getTime(final int MINUTE, final int HOUR);

    public QlockTwo.Language getLanguage();
}
