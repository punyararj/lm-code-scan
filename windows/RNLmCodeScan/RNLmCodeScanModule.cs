using ReactNative.Bridge;
using System;
using System.Collections.Generic;
using Windows.ApplicationModel.Core;
using Windows.UI.Core;

namespace Lm.Code.Scan.RNLmCodeScan
{
    /// <summary>
    /// A module that allows JS to share data.
    /// </summary>
    class RNLmCodeScanModule : NativeModuleBase
    {
        /// <summary>
        /// Instantiates the <see cref="RNLmCodeScanModule"/>.
        /// </summary>
        internal RNLmCodeScanModule()
        {

        }

        /// <summary>
        /// The name of the native module.
        /// </summary>
        public override string Name
        {
            get
            {
                return "RNLmCodeScan";
            }
        }
    }
}
