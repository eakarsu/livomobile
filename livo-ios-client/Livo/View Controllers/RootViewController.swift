//
//  RootViewController.swift
//  Livo
//
//  Created by omur on 11/06/15.
//  Copyright (c) 2015 Livo Mobile. All rights reserved.
//

import UIKit
import SSKeychain
import Tweaks

class RootViewController: UINavigationController, UINavigationControllerDelegate {
    
    private var authenticationSettings : NSMutableDictionary?
    private var authenticationToken : AuthenticationToken?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        log.debug("Loaded root view controller!")
        
        self.delegate = self
        
        //ErrorHelper.reportErrorsViaMail(self)
        
        self.performAuthenticationSettings()
    }
    
    override func viewDidAppear(animated: Bool) {
        
        super.viewDidAppear(animated)
        
        log.debug("Root view controller appeared!")
    }
    
    override func shouldAutorotate() -> Bool {
        
        if self.topViewController != nil {
            
            return self.topViewController!.shouldAutorotate()
            
        } else {
            
            return super.shouldAutorotate()
        }
    }
    
    override func supportedInterfaceOrientations() -> UIInterfaceOrientationMask {
        
        if self.topViewController != nil {
            
            return self.topViewController!.supportedInterfaceOrientations()
            
        } else {
            
            return super.supportedInterfaceOrientations()
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    /*
     // MARK: - Navigation
     
     // In a storyboard-based application, you will often want to do a little preparation before navigation
     override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
     // Get the new view controller using segue.destinationViewController.
     // Pass the selected object to the new view controller.
     }
     */
    
    // MARK: - Navigation controller delegate
    
    func navigationController(navigationController: UINavigationController, animationControllerForOperation operation: UINavigationControllerOperation, fromViewController fromVC: UIViewController, toViewController toVC: UIViewController) -> UIViewControllerAnimatedTransitioning? {
        
        return CrossFadeAnimatator()
    }
    
    func navigationController(navigationController: UINavigationController, willShowViewController viewController: UIViewController, animated: Bool) {
        
        log.debug("Showing view controller '\(viewController.description)'...")
    }
    
    func navigationController(navigationController: UINavigationController, didShowViewController viewController: UIViewController, animated: Bool) {
        
        log.debug("Did show view controller '\(viewController.description)'...")
    }
    
    // MARK: - Helper methods
    var targetViewController : UIViewController = UIViewController()
    func showAppropriateViewController() -> Void {
        
        let mainStoryboard = UIStoryboard(name: "Main", bundle: nil);
        
        let authKey = self.authenticationSettings != nil ? self.authenticationSettings!.allKeys[0] as? String : nil
        log.debug("showAppropriateViewController ---> '\(authKey)'")
        
        var process = ".";
        
        if(authKey != nil){
            
            process = authKey!.componentsSeparatedByString(".")[1];
            log.debug("showAppropriateViewController::process ---> '\(process)' -- \(process.rangeOfString("company"))")
            
            if(process == "any"){
                
                // Construct the service address string
                let serviceAddress = arrangeServiceAddress()
                
                // Clear previous values
                self.clearKeychainAccounts("CA:\(serviceAddress)")
                self.clearKeychainAccounts("UA:\(serviceAddress)")
                
                // Save the company ID and secret
                SSKeychain.setPassword("LivoAny", forService: "CA:\(serviceAddress)", account: "LivoAny")
                // Save the user principal and credentials
                SSKeychain.setPassword("LivoAnY", forService: "UA:\(serviceAddress)", account: "livoany")
                
                targetViewController = mainStoryboard.instantiateViewControllerWithIdentifier("SilentLoginViewController") as! SilentLoginViewController
                
            }
            else
            if(process == "company"){
                
                // Construct the service address string
                let serviceAddress = arrangeServiceAddress()
                
                // Clear previous values
                self.clearKeychainAccounts("UA:\(serviceAddress)")
                
                // Save the user principal and credentials
                SSKeychain.setPassword("LivoComP", forService: "UA:\(serviceAddress)", account: "livocomp")
                
                targetViewController = mainStoryboard.instantiateViewControllerWithIdentifier("CompanyLoginViewController") as! CompanyLoginViewController
                
            }
            else
            if(process == "System" || process == "ldap"){
                
                self.performCompanyAuthentication("LivoUsers", companySecret: "LivoUsers")
                
            }
            else{
                
                targetViewController = mainStoryboard.instantiateViewControllerWithIdentifier("CompanyLoginViewController") as! CompanyLoginViewController
                    
            }
            
        }
        
        if !serverSettingsValid() {
            
            targetViewController = mainStoryboard.instantiateViewControllerWithIdentifier("SettingsHelpViewController") as! SettingsHelpViewController
            
        } else if authenticationRequired() {
            
            if authenticationSilent() {
                
                log.debug("showAppropriateViewController::authenticationSilent")
                
                targetViewController = mainStoryboard.instantiateViewControllerWithIdentifier("SilentLoginViewController") as! SilentLoginViewController
                
            }
            
        }
            //This is removed by omur because is not pointless under current architect
            /*else if updateRequired() {
             
             targetViewController = mainStoryboard.instantiateViewControllerWithIdentifier("UpdateViewController") as! UpdateViewController
             
             }*/
        else {
            
            print("No eligible target view controller found.")
            
        }
        
        if(process != "System" && process != "ldap"){
            dispatch_async(dispatch_get_main_queue(), {
                self.pushViewController(self.targetViewController, animated: true)
            })
        }
        
    }
    
    private func serverSettingsValid() -> Bool {
        
        let host = NSUserDefaults.standardUserDefaults().stringForKey(Settings.Server.Host)
        let port = NSUserDefaults.standardUserDefaults().integerForKey(Settings.Server.Port)
        
        log.debug(" ---> '\(host)' -- '\(port)'")
        return host != nil && !(host!.stringByTrimmingCharactersInSet(NSCharacterSet.whitespaceAndNewlineCharacterSet()).isEmpty) && port > 0
    }
    
    private func authenticationRequired() -> Bool {
        
        let authenticationEnabled = NSUserDefaults.standardUserDefaults().integerForKey(Settings.Authentication.Level) > 0
        
        return authenticationEnabled
    }
    
    private func authenticationSilent() -> Bool {
        
        let authenticationSilent = NSUserDefaults.standardUserDefaults().boolForKey(Settings.Authentication.Silent)
        log.debug("authenticationSilent ---> \(authenticationSilent)")
        
        return authenticationSilent
    }
    
    private func updateRequired() -> Bool {
        
        return true
    }
    
    private func arrangeServiceAddress() -> String {
        
        // Get the server settings
        let host = NSUserDefaults.standardUserDefaults().stringForKey(Settings.Server.Host)!
        let port = NSUserDefaults.standardUserDefaults().integerForKey(Settings.Server.Port)
        
        // Construct the service address string
        let serviceAddress = "\(host):\(port)"
        
        return serviceAddress
    }
    
    private func performAuthenticationSettings() {
        
        // Get the server settings
        let applicationId : String? = NSUserDefaults.standardUserDefaults().stringForKey(Settings.Application.Identifier)
        
        log.debug("Authentication settings for '\(applicationId)' -- '\(applicationId?.rangeOfString("default"))'...")
        
        if applicationId != nil && !applicationId!.isEmpty && applicationId?.rangeOfString("default") == nil {
            
            // Perform authentication
            ServiceHelper.authenticateSetting(applicationId!, success: { (map) in
                
                log.debug("Successfully received authentication settings for '\(applicationId)'.")
                
                self.authenticationSettings = map
                log.debug("map ---> '\(self.authenticationSettings)'.")
                
                self.showAppropriateViewController()
                
            }) { (message) -> Void in
                
                log.debug("To receive uthentication settings is failed for '\(applicationId)'.")
                
                self.handleError(message!, nextViewControllerIdentifier:"SettingsHelpViewController")
            }
            
        }
        else{
            self.showAppropriateViewController()
        }
        
    }
    
    private func handleError(message: String, nextViewControllerIdentifier: String) {
        
        NSOperationQueue.mainQueue().addOperationWithBlock({ () -> Void in
            
            let alert = UIAlertController(title: NSLocalizedString("Authentication Failed", comment: ""), message: message, preferredStyle: .Alert)
            
            let alertAction = UIAlertAction(title: NSLocalizedString("OK", comment: ""), style: .Default, handler: { (action : UIAlertAction!) -> Void in
                
                alert.dismissViewControllerAnimated(true, completion: nil)
                
                // Instantiate the next view controller
                let nextViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewControllerWithIdentifier(nextViewControllerIdentifier)
                
                // Present the view controller
                self.navigationController!.pushViewController(nextViewController, animated: true)
            })
            
            alert.addAction(alertAction)
            alert.view.tintColor = UIColor.grayColor()
            
            self.presentViewController(alert, animated: true, completion: {
                
                self.navigationController!.popViewControllerAnimated(true)
            })
        })
    }
    
    private func clearKeychainAccounts(serviceName: String) {
        
        // Get accounts for service
        let accounts = SSKeychain.accountsForService(serviceName) ?? NSArray()
        
        // Iterate through the accounts
        for account in accounts {
            
            // Get the first account available
            let accountDict = account as! NSDictionary
            
            // Get the account string from the account
            let accountStr = accountDict[NSString(format: kSecAttrAccount)] as! String
            
            // Delete account data
            SSKeychain.deletePasswordForService(serviceName, account: accountStr)
        }
    }
    
    private func performCompanyAuthentication(companyId: String, companySecret: String) {
        
        log.debug("performCompanyAuthentication: company '\(companyId)'...")
        
        // Get the configured application ID
        let applicationId = NSUserDefaults.standardUserDefaults().stringForKey(Settings.Application.Identifier)
        
        // Perform authentication
        ServiceHelper.authenticateCompany(companyId, companySecret: companySecret, appName: applicationId!, success: { (token) -> Void in
            
            log.debug("Successfully completed company authentication with ID '\(companyId)'.")
            
            self.authenticationToken = token
            
            let nextViewController : UserLoginViewController = UIStoryboard(name: "Main", bundle: nil).instantiateViewControllerWithIdentifier("UserLoginViewController") as! UserLoginViewController
            nextViewController.authenticationToken = self.authenticationToken
            
            self.targetViewController = nextViewController
            
            dispatch_async(dispatch_get_main_queue(), {
                self.pushViewController(self.targetViewController, animated: true)
            })
            
        }) { (message) -> Void in
            
            log.debug("Company authentication failed for ID '\(companyId)'.")
            
            self.handleError(message!, nextViewControllerIdentifier:"CompanyLoginViewController")
        }
    }
    
}
